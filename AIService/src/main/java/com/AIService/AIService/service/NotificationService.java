package com.AIService.AIService.service;


import com.AIService.AIService.dto.InterviewDTOs;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void pushQuestionsReady(UUID userId, UUID sessionId, List<InterviewDTOs.QuestionResponse> questions);

    void pushEvaluationDone(UUID userId, UUID sessionId,
                            InterviewDTOs.EvaluationResponse evaluation, Double currentAvgScore);

    void pushSessionComplete(UUID userId, UUID sessionId,
                             Double overallScore, String reportUrl);

    void pushError(UUID sessionId, String errorMessage);
}