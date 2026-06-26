package com.AIService.AIService.serviceImpl;

import com.AIService.AIService.dto.InterviewDTOs;
import com.AIService.AIService.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Pushes real-time events to the frontend via STOMP over WebSocket.
 *
 * Client subscribes to:
 *   /user/{userId}/queue/interview   ← user-specific events
 *   /topic/interview/{sessionId}     ← session-level broadcast (optional)
 *
 * Event types pushed:
 *   QUESTIONS_READY   → after Groq generates all questions
 *   EVALUATION_DONE   → after each answer is evaluated
 *   SESSION_COMPLETE  → when all evaluations are done and report is ready
 *   ERROR             → on any async failure
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // Destination the React client subscribes to (user-specific queue)
    private static final String USER_QUEUE = "/queue/interview";

    // ─── Push events ──────────────────────────────────────────────────────────

    @Override
    public void pushQuestionsReady(UUID userId, UUID sessionId,
                                   List<InterviewDTOs.QuestionResponse> questions) {
        InterviewDTOs.WsQuestionsReady payload = InterviewDTOs.WsQuestionsReady.builder()
                .type("QUESTIONS_READY")
                .sessionId(sessionId)
                .questions(questions)
                .build();

        sendToUser(userId, payload);
        log.info("[WS] QUESTIONS_READY pushed to userId={} sessionId={}", userId, sessionId);
    }

    @Override
    public void pushEvaluationDone(UUID userId, UUID sessionId,
                                   InterviewDTOs.EvaluationResponse evaluation,
                                   Double currentAvgScore) {
        InterviewDTOs.WsEvaluationDone payload = InterviewDTOs.WsEvaluationDone.builder()
                .type("EVALUATION_DONE")
                .sessionId(sessionId)
                .evaluation(evaluation)
                .currentAvgScore(currentAvgScore)
                .build();

        sendToUser(userId, payload);
        log.info("[WS] EVALUATION_DONE pushed: sessionId={} score={}",
                sessionId, evaluation.getScore());
    }

    @Override
    public void pushSessionComplete(UUID userId, UUID sessionId,
                                    Double overallScore, String reportUrl) {
        InterviewDTOs.WsSessionComplete payload = InterviewDTOs.WsSessionComplete.builder()
                .type("SESSION_COMPLETE")
                .sessionId(sessionId)
                .overallScore(overallScore)
                .reportUrl(reportUrl)
                .build();

        sendToUser(userId, payload);
        log.info("[WS] SESSION_COMPLETE pushed: sessionId={} score={}", sessionId, overallScore);
    }

    // In WebSocketNotificationService.java — replace the raw Map with this record

    record ErrorPayload(String type, UUID sessionId, String message) {}

    @Override
    public void pushError(UUID sessionId, String errorMessage) {
        ErrorPayload payload = new ErrorPayload("ERROR", sessionId, errorMessage);

        // No ambiguity — ErrorPayload is a concrete type, not Map<String, Object>
        messagingTemplate.convertAndSend(
                "/topic/interview/" + sessionId, payload);

        log.warn("[WS] ERROR pushed to sessionId={}: {}", sessionId, errorMessage);
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private void sendToUser(UUID userId, Object payload) {
        // SimpMessagingTemplate routes to /user/{userId}/queue/interview
        messagingTemplate.convertAndSendToUser(
                userId.toString(), USER_QUEUE, payload);
    }
}