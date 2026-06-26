package com.AIService.AIService.service;


import com.AIService.AIService.dto.InterviewDTOs;

import java.util.List;
import java.util.UUID;

public interface InterviewService {

    /** Create a session and trigger async question generation. */
    InterviewDTOs.SessionResponse startSession(UUID userId, InterviewDTOs.StartSessionRequest request, String bearerToken);

    /** Return all questions for a session (only once status = QUESTIONS_READY). */
    List<InterviewDTOs.QuestionResponse> getQuestions(UUID sessionId, UUID userId);

    /** Submit an answer; triggers async evaluation of that answer. */
    InterviewDTOs.AnswerSubmittedResponse submitAnswer(UUID sessionId, UUID userId,
                                                       InterviewDTOs.SubmitAnswerRequest request);

    /** Return final result with all evaluations and report URL. */
    InterviewDTOs.SessionResultResponse getResult(UUID sessionId, UUID userId);

    /** Return all sessions for a user (history). */
    List<InterviewDTOs.SessionResponse> getUserSessions(UUID userId);
}
