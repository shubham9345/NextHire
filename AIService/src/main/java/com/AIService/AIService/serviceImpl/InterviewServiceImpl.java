package com.AIService.AIService.serviceImpl;

import com.AIService.AIService.client.JobServiceClient;
import com.AIService.AIService.client.UserServiceClient;
import com.AIService.AIService.dto.InterviewDTOs;
import com.AIService.AIService.entity.InterviewAnswer;
import com.AIService.AIService.entity.InterviewEvaluation;
import com.AIService.AIService.entity.InterviewQuestion;
import com.AIService.AIService.entity.InterviewSession;
import com.AIService.AIService.enums.InterviewEnums;
import com.AIService.AIService.repository.InterviewAnswerRepository;
import com.AIService.AIService.repository.InterviewEvaluationRepository;
import com.AIService.AIService.repository.InterviewQuestionRepository;
import com.AIService.AIService.repository.InterviewSessionRepository;
import com.AIService.AIService.service.InterviewService;
import com.AIService.AIService.service.LlmService;
import com.AIService.AIService.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewServiceImpl implements InterviewService {

    private final InterviewSessionRepository sessionRepo;
    private final InterviewQuestionRepository questionRepo;
    private final InterviewAnswerRepository answerRepo;
    private final InterviewEvaluationRepository evalRepo;

    private final LlmService llmService;
    private final InterviewSessionCacheService cache;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @Autowired
    private final JobServiceClient jobClient;
    @Autowired
    private final UserServiceClient userClient;

    @Value("${interview.max-concurrent-per-user:2}")
    private int maxConcurrentPerUser;

    // ─── Start session ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public InterviewDTOs.SessionResponse startSession(UUID userId,
                                                      InterviewDTOs.StartSessionRequest request,
                                                      String bearerToken) {
        // Rate limit: don't allow too many active sessions per user
        long activeCount = sessionRepo.countActiveSessionsByUser(userId);
        if (activeCount >= maxConcurrentPerUser) {
            throw new IllegalStateException(
                    "You already have " + activeCount + " active interview session(s). " +
                            "Complete or abandon them before starting a new one.");
        }

        // Fetch job and user context (via Feign; fallback stubs if downstream is down)
        InterviewDTOs.JobDetailDTO job = jobClient.getJobById(request.getJobId(), bearerToken);
        InterviewDTOs.UserProfileDTO user = userClient.getUserProfile(userId, bearerToken);

        // Persist the session
        InterviewSession session = InterviewSession.builder()
                .userId(userId)
                .jobId(request.getJobId())
                .jobTitle(job.getTitle())
                .jobDescription(job.getDescription())
                .requiredSkills(job.getRequiredSkills())
                .status(InterviewEnums.SessionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .email(user.getEmail())
                .build();
        session = sessionRepo.save(session);

        // Kick off async question generation — returns immediately to caller
        generateQuestionsAsync(session.getId(), job, user, request.getQuestionCount());

        log.info("Interview session created: sessionId={} userId={} jobId={}",
                session.getId(), userId, request.getJobId());

        SendStartSessionEmail(user.getEmail());
        log.info("Interview start email send to {}", user.getEmail());

        return InterviewDTOs.SessionResponse.builder()
                .sessionId(session.getId())
                .jobId(session.getJobId())
                .jobTitle(session.getJobTitle())
                .status(session.getStatus())
                .createdAt(session.getCreatedAt())
                .message("Questions are being generated. " +
                        "You will be notified via WebSocket when ready.")
                .build();
    }

    // ─── Async question generation ────────────────────────────────────────────

    @Async("interviewTaskExecutor")
    public void generateQuestionsAsync(UUID sessionId,
                                       InterviewDTOs.JobDetailDTO job,
                                       InterviewDTOs.UserProfileDTO user,
                                       int questionCount) {
        log.info("[ASYNC] Starting question generation for sessionId={}", sessionId);
        try {
            // Call Groq LLM
            List<InterviewDTOs.LlmQuestion> llmQuestions = llmService.generateQuestions(
                    job.getTitle(),
                    job.getDescription(),
                    job.getRequiredSkills(),
                    user.getSkills(),
                    questionCount
            );

            // Persist questions
            InterviewSession session = sessionRepo.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

            List<InterviewQuestion> questions = IntStream
                    .range(0, llmQuestions.size())
                    .mapToObj(i -> {
                        InterviewDTOs.LlmQuestion lq = llmQuestions.get(i);
                        return InterviewQuestion.builder()
                                .session(session)
                                .questionText(lq.getQuestionText())
                                .questionType(safeParseQuestionType(lq.getQuestionType()))
                                .difficulty(safeParseDifficulty(lq.getDifficulty()))
                                .expectedKeyPoints(toJson(lq.getExpectedKeyPoints()))
                                .orderIndex(i)
                                .build();
                    })
                    .toList();

            questionRepo.saveAll(questions);

            // Update session status
            session.setStatus(InterviewEnums.SessionStatus.QUESTIONS_READY);
            sessionRepo.save(session);

            // Initialise session cache state
            cache.initSession(sessionId, session.getUserId(), questions.size());

            // Push QUESTIONS_READY event to client via WebSocket
            List<InterviewDTOs.QuestionResponse> qResponses = toQuestionResponses(questions);
            notificationService.pushQuestionsReady(session.getUserId(), sessionId, qResponses);

            log.info("[ASYNC] Question generation complete: sessionId={} count={}",
                    sessionId, questions.size());

        } catch (Exception e) {
            log.error("[ASYNC] Question generation failed for sessionId={}", sessionId, e);
            sessionRepo.findById(sessionId).ifPresent(s -> {
                s.setStatus(InterviewEnums.SessionStatus.FAILED);
                sessionRepo.save(s);
            });
            notificationService.pushError(sessionId, "Question generation failed: " + e.getMessage());
        }
    }

    // ─── Get questions ────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<InterviewDTOs.QuestionResponse> getQuestions(UUID sessionId, UUID userId) {
        InterviewSession session = getSessionOwnedBy(sessionId, userId);

        if (session.getStatus() == InterviewEnums.SessionStatus.PENDING) {
            throw new IllegalStateException("Questions are still being generated. Please wait.");
        }

        List<InterviewQuestion> questions =
                questionRepo.findBySessionIdOrderByOrderIndex(sessionId);

        return toQuestionResponses(questions);
    }

    // ─── Submit answer ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public InterviewDTOs.AnswerSubmittedResponse submitAnswer(UUID sessionId,
                                                              UUID userId,
                                                              InterviewDTOs.SubmitAnswerRequest request) {
        InterviewSession session = getSessionOwnedBy(sessionId, userId);

        if (session.getStatus() == InterviewEnums.SessionStatus.PENDING ||
                session.getStatus() == InterviewEnums.SessionStatus.QUESTIONS_READY) {
            // Mark as in-progress on first answer
            session.setStatus(InterviewEnums.SessionStatus.IN_PROGRESS);
            session.setStartedAt(LocalDateTime.now());
            sessionRepo.save(session);
        }

        if (session.getStatus() == InterviewEnums.SessionStatus.COMPLETED ||
                session.getStatus() == InterviewEnums.SessionStatus.EVALUATING) {
            throw new IllegalStateException("Session is already closed for new answers.");
        }

        InterviewQuestion question = questionRepo.findById(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Question not found: " + request.getQuestionId()));

        // Prevent duplicate answer submission
        if (answerRepo.findByQuestionId(request.getQuestionId()).isPresent()) {
            throw new IllegalStateException("Answer already submitted for this question.");
        }

        // At least one of text or audio must be present
        if ((request.getAnswerText() == null || request.getAnswerText().isBlank())
                && (request.getAudioUrl() == null || request.getAudioUrl().isBlank())) {
            throw new IllegalArgumentException(
                    "Provide either answerText or audioUrl (Firebase URL).");
        }

        InterviewAnswer answer = InterviewAnswer.builder()
                .question(question)
                .answerText(request.getAnswerText())
                .audioUrl(request.getAudioUrl())
                .build();
        answer = answerRepo.save(answer);

        // Advance session index
        cache.incrementCurrentIndex(sessionId);
        int answeredSoFar = cache.incrementAndGetAnswersCount(sessionId);
        boolean allAnswered = cache.allAnswered(sessionId);

        // Trigger async evaluation for this answer
        evaluateAnswerAsync(answer.getId(), question, session);

        // If all questions answered, mark session as evaluating
        if (allAnswered) {
            session.setStatus(InterviewEnums.SessionStatus.COMPLETED);
            sessionRepo.save(session);
            log.info("All answers received for sessionId={}, evaluation in progress", sessionId);
        }

        return InterviewDTOs.AnswerSubmittedResponse.builder()
                .answerId(answer.getId())
                .questionId(request.getQuestionId())
                .allAnswered(allAnswered)
                .message(allAnswered
                        ? "All answers submitted! Evaluation in progress."
                        : "Answer recorded. " + answeredSoFar + " of " +
                        cache.getTotalQuestions(sessionId) + " answered.")
                .build();
    }

    // ─── Async answer evaluation ──────────────────────────────────────────────

    @Async("interviewTaskExecutor")
    public void evaluateAnswerAsync(UUID answerId,
                                    InterviewQuestion question,
                                    InterviewSession session) {
        log.info("[ASYNC] Evaluating answerId={} questionId={}", answerId, question.getId());
        try {
            InterviewAnswer answer = answerRepo.findById(answerId)
                    .orElseThrow(() -> new RuntimeException("Answer not found: " + answerId));

            // Create a pending evaluation record first
            InterviewEvaluation evaluation = InterviewEvaluation.builder()
                    .answer(answer)
                    .score(0.0)
                    .status(InterviewEnums.EvaluationStatus.IN_PROGRESS)
                    .build();
            evaluation = evalRepo.save(evaluation);

            // Call Groq
            InterviewDTOs.LlmEvaluation llmResult = llmService.evaluateAnswer(
                    question.getQuestionText(),
                    question.getExpectedKeyPoints(),
                    answer.getAnswerText() != null
                            ? answer.getAnswerText()
                            : "[Audio answer — transcript not available]"
            );

            // Persist result
            evaluation.setScore(llmResult.getScore());
            evaluation.setFeedback(llmResult.getFeedback());
            evaluation.setRubricJson(toJson(llmResult.getRubric()));
            evaluation.setStatus(InterviewEnums.EvaluationStatus.DONE);
            evalRepo.save(evaluation);

            // Push real-time feedback to client
            Double avgScore = evalRepo.averageScoreBySessionId(session.getId());
            notificationService.pushEvaluationDone(
                    session.getUserId(), session.getId(),
                    buildEvalResponse(evaluation, question, answer),
                    avgScore
            );

            // Check if all evaluations are complete → generate final report
            long totalQ = questionRepo.countBySessionId(session.getId());
            long doneEvals = evalRepo.findAllBySessionId(session.getId())
                    .stream()
                    .filter(e -> e.getStatus() == InterviewEnums.EvaluationStatus.DONE)
                    .count();

            if (doneEvals >= totalQ) {
                generateReportAsync(session.getId(), session.getUserId());
                sendInterviewCompletionEmail(session.getEmail());
                log.info("Interview Completion email send to {}", session.getEmail());
            }

        } catch (Exception e) {
            log.error("[ASYNC] Evaluation failed for answerId={}", answerId, e);
            evalRepo.findByAnswerId(answerId).ifPresent(ev -> {
                ev.setStatus(InterviewEnums.EvaluationStatus.FAILED);
                ev.setFeedback("Evaluation failed: " + e.getMessage());
                evalRepo.save(ev);
            });
        }
    }

    // ─── Async report generation ──────────────────────────────────────────────

    @Async("interviewTaskExecutor")
    public void generateReportAsync(UUID sessionId, UUID userId) {
        log.info("[ASYNC] Generating final report for sessionId={}", sessionId);
        try {
            InterviewSession session = sessionRepo.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Session not found"));

            List<InterviewEvaluation> evals = evalRepo.findAllBySessionId(sessionId);
            Double avgScore = evalRepo.averageScoreBySessionId(sessionId);

            List<String> feedbacks = evals.stream()
                    .map(InterviewEvaluation::getFeedback)
                    .toList();

            String summary = llmService.generateReportSummary(
                    session.getJobTitle(),
                    avgScore != null ? avgScore : 0.0,
                    feedbacks
            );

            String reportPlaceholderUrl = "report-pending-pdf-generation";
            session.setOverallScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0);
            session.setReportUrl(reportPlaceholderUrl);
            session.setStatus(InterviewEnums.SessionStatus.COMPLETED);
            session.setCompletedAt(LocalDateTime.now());
            sessionRepo.save(session);

            // Clear session cache state
            cache.clearSession(sessionId, userId);

            // Push SESSION_COMPLETE to client
            notificationService.pushSessionComplete(userId, sessionId, avgScore, reportPlaceholderUrl);

            log.info("[ASYNC] Report generation complete for sessionId={} score={}",
                    sessionId, avgScore);

        } catch (Exception e) {
            log.error("[ASYNC] Report generation failed for sessionId={}", sessionId, e);
        }
    }

    // ─── Get result ───────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public InterviewDTOs.SessionResultResponse getResult(UUID sessionId, UUID userId) {
        InterviewSession session = getSessionOwnedBy(sessionId, userId);
        List<InterviewEvaluation> evals = evalRepo.findAllBySessionId(sessionId);

        List<InterviewDTOs.EvaluationResponse> evalResponses = evals.stream()
                .map(ev -> {
                    InterviewAnswer ans = ev.getAnswer();
                    InterviewQuestion q = ans.getQuestion();
                    return buildEvalResponse(ev, q, ans);
                })
                .toList();

        return InterviewDTOs.SessionResultResponse.builder()
                .sessionId(sessionId)
                .jobTitle(session.getJobTitle())
                .status(session.getStatus())
                .overallScore(session.getOverallScore())
                .reportUrl(session.getReportUrl())
                .evaluations(evalResponses)
                .completedAt(session.getCompletedAt())
                .build();
    }

    // ─── Get all sessions for user ────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<InterviewDTOs.SessionResponse> getUserSessions(UUID userId) {
        return sessionRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(s -> InterviewDTOs.SessionResponse.builder()
                        .sessionId(s.getId())
                        .jobId(s.getJobId())
                        .jobTitle(s.getJobTitle())
                        .status(s.getStatus())
                        .createdAt(s.getCreatedAt())
                        .build())
                .toList();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private InterviewSession getSessionOwnedBy(UUID sessionId, UUID userId) {
        InterviewSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Session not found: " + sessionId));
        if (!session.getUserId().equals(userId)) {
            throw new SecurityException("Access denied to session: " + sessionId);
        }
        return session;
    }

    private List<InterviewDTOs.QuestionResponse> toQuestionResponses(List<InterviewQuestion> questions) {
        int total = questions.size();
        return questions.stream()
                .map(q -> InterviewDTOs.QuestionResponse.builder()
                        .questionId(q.getId())
                        .questionText(q.getQuestionText())
                        .questionType(q.getQuestionType())
                        .difficulty(q.getDifficulty())
                        .orderIndex(q.getOrderIndex())
                        .totalQuestions(total)
                        .build())
                .toList();
    }

    private InterviewDTOs.EvaluationResponse buildEvalResponse(InterviewEvaluation ev,
                                                               InterviewQuestion q,
                                                               InterviewAnswer ans) {
        return InterviewDTOs.EvaluationResponse.builder()
                .evaluationId(ev.getId())
                .questionId(q.getId())
                .questionText(q.getQuestionText())
                .answerText(ans.getAnswerText())
                .score(ev.getScore())
                .feedback(ev.getFeedback())
                .rubric(fromJson(ev.getRubricJson()))
                .status(ev.getStatus())
                .build();
    }

    private InterviewEnums.QuestionType safeParseQuestionType(String val) {
        try {
            return InterviewEnums.QuestionType.valueOf(val.toUpperCase());
        } catch (Exception e) {
            return InterviewEnums.QuestionType.TECHNICAL;
        }
    }

    private InterviewEnums.Difficulty safeParseDifficulty(String val) {
        try {
            return InterviewEnums.Difficulty.valueOf(val.toUpperCase());
        } catch (Exception e) {
            return InterviewEnums.Difficulty.MEDIUM;
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }

    private Object fromJson(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            return json;
        }
    }
    @Async
    private void SendStartSessionEmail(String candidateEmail){
        // Interview Started
        String subject = "Your Interview Has Started";

        String body = """
                Dear Candidate,
                
                Your interview session has started successfully.
                
                Please ensure that:
                - You have a stable internet connection.
                - Your camera and microphone remain enabled throughout the interview.
                - You complete the interview within the allotted time.
                - You avoid refreshing or closing the browser window during the session.
                
                We wish you the very best for your interview!
                
                Best regards,
                NextHire Management Team
                """;

        emailService.sendUpdateMail(candidateEmail, subject, body);
    }
    @Async
    private void sendInterviewCompletionEmail(String candidateEmail){
        // Interview Completed
        String subject = "Interview Completed Successfully";

        String body = """
                Dear Candidate,
                
                Congratulations! You have successfully completed your interview.
                
                Thank you for taking the time to participate. Your responses have been recorded and will be reviewed by our team.
                
                You will be notified about the next steps once the evaluation process is complete.
                
                We appreciate your interest and wish you all the best.
                
                Best regards,
                NextHire Management Team
                """;

        emailService.sendUpdateMail(candidateEmail, subject, body);
    }
}
