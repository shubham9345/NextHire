package com.AIService.AIService.dto;


import com.AIService.AIService.enums.InterviewEnums;
import com.AIService.AIService.enums.JobStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class InterviewDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StartSessionRequest {
        @NotNull(message = "jobId is required")
        private UUID jobId;

        // Optional: override default question count (5–10)
        @Min(2)
        @Max(10)
        private Integer questionCount = 7;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmitAnswerRequest {
        @NotNull(message = "questionId is required")
        private UUID questionId;

        // At least one of answerText or audioUrl must be provided
        private String answerText;

        // Firebase URL to audio blob (uploaded by client before calling this)
        private String audioUrl;
    }

    // ─── Responses ───────────────────────────────────────────────────────────

    @Data
    @Builder
    public static class SessionResponse {
        private UUID sessionId;
        private UUID jobId;
        private String jobTitle;
        private InterviewEnums.SessionStatus status;
        @CreationTimestamp
        private LocalDateTime createdAt;
        private String message;   // e.g. "Questions are being generated..."
    }

    @Data
    @Builder
    public static class QuestionResponse {
        private UUID questionId;
        private String questionText;
        private InterviewEnums.QuestionType questionType;
        private InterviewEnums.Difficulty difficulty;
        private int orderIndex;
        private int totalQuestions;
    }

    @Data
    @Builder
    public static class AnswerSubmittedResponse {
        private UUID answerId;
        private UUID questionId;
        private String message;
        private boolean allAnswered;   // triggers final evaluation if true
    }

    @Data
    @Builder
    public static class EvaluationResponse {
        private UUID evaluationId;
        private UUID questionId;
        private String questionText;
        private String answerText;
        private Double score;
        private String feedback;
        private Object rubric;          // deserialized rubric map
        private InterviewEnums.EvaluationStatus status;
    }

    @Data
    @Builder
    public static class SessionResultResponse {
        private UUID sessionId;
        private String jobTitle;
        private InterviewEnums.SessionStatus status;
        private Double overallScore;
        private String reportUrl;
        private List<EvaluationResponse> evaluations;
        @CreationTimestamp
        private LocalDateTime completedAt;
    }

    // ─── WebSocket push payloads ─────────────────────────────────────────────

    @Data
    @Builder
    public static class WsQuestionsReady {
        private String type;           // "QUESTIONS_READY"
        private UUID sessionId;
        private List<QuestionResponse> questions;
    }

    @Data
    @Builder
    public static class WsEvaluationDone {
        private String type;           // "EVALUATION_DONE" | "SESSION_COMPLETE"
        private UUID sessionId;
        private EvaluationResponse evaluation;
        private Double currentAvgScore;
    }

    @Data
    @Builder
    public static class WsSessionComplete {
        private String type;           // "SESSION_COMPLETE"
        private UUID sessionId;
        private Double overallScore;
        private String reportUrl;
    }

    // ─── Internal / Feign DTOs ───────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobDetailDTO {
        private UUID id;

        private UUID companyId;

        private String title;

        private String description;

        private String location;

        private String jobType;

        private String experienceLevel;

        private Integer salaryMin;

        private Integer salaryMax;

        private Integer maxCandidates;

        private Integer appliedCandidates;

        private JobStatus status;
        private String requiredSkills;
        @CreationTimestamp
        private LocalDateTime createdAt;
        @CreationTimestamp
        private Instant stoppedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileDTO {
        private UUID id;
        private UUID authUserId;
        private String fullName;
        private String phoneNumber;
        private String headline;
        private String bio;
        private String email;
        private String location;
        private Integer yearsOfExperience;
        private String skills;
        private String resumeUrl;
        private String linkedinUrl;
        private String githubUrl;
        private LocalDate dateOfBirth;
        private LocalDateTime createdAt;
    }

    // ─── LLM-parsed question (internal) ──────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LlmQuestion {
        private String questionText;
        private String questionType;      // maps to QuestionType enum
        private String difficulty;        // maps to Difficulty enum
        private List<String> expectedKeyPoints;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LlmEvaluation {
        private Double score;
        private String feedback;
        private LlmRubric rubric;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LlmRubric {
        private Integer accuracy;
        private Integer depth;
        private Integer clarity;
        private Integer relevance;
    }
}