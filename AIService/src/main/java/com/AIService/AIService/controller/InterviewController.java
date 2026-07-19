package com.AIService.AIService.controller;


import com.AIService.AIService.dto.InterviewDTOs;
import com.AIService.AIService.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * All endpoints are prefixed /api/interviews.
 * Spring Cloud Gateway routes lb://INTERVIEW-SERVICE → here.
 * <p>
 * JWT is validated at the Gateway; the userId is forwarded in the
 * X-User-Id header (or extracted from SecurityContext — both shown).
 */
@RestController
@RequestMapping("/api/ai/interviews")
@RequiredArgsConstructor
@Slf4j
public class InterviewController {

    private final InterviewService interviewService;

    /**
     * POST /api/interviews/start
     * Body: { "jobId": "uuid", "questionCount": 7 }
     * Returns immediately with sessionId; questions arrive via WebSocket.
     */
    @PostMapping("/start")
    public ResponseEntity<InterviewDTOs.SessionResponse> startSession(
            @Valid @RequestBody InterviewDTOs.StartSessionRequest request,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("Authorization") String bearerToken) {

        log.info("Start interview session: userId={} jobId={}", userId, request.getJobId());
        InterviewDTOs.SessionResponse response = interviewService.startSession(userId, request, bearerToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/interviews/{sessionId}/questions
     * Polls for questions if client missed the WebSocket push.
     * Returns 202 ACCEPTED while status is still PENDING.
     */
    @GetMapping("/{sessionId}/questions")
    public ResponseEntity<List<InterviewDTOs.QuestionResponse>> getQuestions(
            @PathVariable UUID sessionId,
            @RequestHeader("X-User-Id") UUID userId) {

        try {
            List<InterviewDTOs.QuestionResponse> questions =
                    interviewService.getQuestions(sessionId, userId);
            return ResponseEntity.ok(questions);
        } catch (IllegalStateException e) {
            // Questions still generating
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
    }

    /**
     * POST /api/interviews/{sessionId}/answer
     * Body: { "questionId": "uuid", "answerText": "..." }
     * OR   { "questionId": "uuid", "audioUrl": "https://firebase/..." }
     */
    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<InterviewDTOs.AnswerSubmittedResponse> submitAnswer(
            @PathVariable UUID sessionId,
            @Valid @RequestBody InterviewDTOs.SubmitAnswerRequest request,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Answer submitted: sessionId={} questionId={} userId={}",
                sessionId, request.getQuestionId(), userId);
        InterviewDTOs.AnswerSubmittedResponse response =
                interviewService.submitAnswer(sessionId, userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/interviews/{sessionId}/result
     * Returns full result with all evaluations once session is COMPLETED.
     * Returns 202 ACCEPTED while still EVALUATING.
     */
    @GetMapping("/{sessionId}/result")
    public ResponseEntity<InterviewDTOs.SessionResultResponse> getResult(
            @PathVariable UUID sessionId,
            @RequestHeader("X-User-Id") UUID userId) {

        InterviewDTOs.SessionResultResponse result = interviewService.getResult(sessionId, userId);

        if (result.getStatus().name().equals("EVALUATING") ||
                result.getStatus().name().equals("IN_PROGRESS")) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
        }

        return ResponseEntity.ok(result);
    }
    @GetMapping("/session-ids/{jobId}")
    public ResponseEntity<List<UUID>> getSessionIdsByJobId(
            @PathVariable UUID jobId) {

        List<UUID> sessionIds =
                interviewService.getAllSessionId(jobId);

        return ResponseEntity.ok(sessionIds);
    }

    /**
     * GET /api/interviews/history
     * Returns all past sessions for the authenticated user.
     */
    @GetMapping("/history")
    public ResponseEntity<List<InterviewDTOs.SessionResponse>> getHistory(
            @RequestHeader("X-User-Id") UUID userId) {

        return ResponseEntity.ok(interviewService.getUserSessions(userId));
    }
}