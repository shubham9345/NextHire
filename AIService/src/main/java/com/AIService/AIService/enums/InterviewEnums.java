package com.AIService.AIService.enums;


public class InterviewEnums {

    public enum SessionStatus {
        PENDING,        // created, questions not yet generated
        QUESTIONS_READY,// LLM returned questions, waiting for candidate
        IN_PROGRESS,    // candidate has started answering
        EVALUATING,     // all answers submitted, async eval running
        COMPLETED,      // report ready
        ABANDONED,      // timed out or cancelled
        FAILED          // LLM/system error
    }

    public enum QuestionType {
        TECHNICAL,
        BEHAVIORAL,
        SITUATIONAL,
        SYSTEM_DESIGN,
        CODING
    }

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    public enum EvaluationStatus {
        PENDING,
        IN_PROGRESS,
        DONE,
        FAILED
    }
}