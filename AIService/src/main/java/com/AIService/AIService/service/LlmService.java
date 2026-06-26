package com.AIService.AIService.service;


import com.AIService.AIService.dto.InterviewDTOs;

import java.util.List;

public interface LlmService {

    /**
     * Generate interview questions for a given job context.
     *
     * @param jobTitle       title of the role
     * @param jobDescription full JD text
     * @param requiredSkills comma-separated skills
     * @param userSkills     candidate's skills from profile
     * @param questionCount  how many questions to generate
     * @return parsed list of questions
     */
    List<InterviewDTOs.LlmQuestion> generateQuestions(
            String jobTitle,
            String jobDescription,
            String requiredSkills,
            String userSkills,
            int questionCount
    );

    /**
     * Evaluate a single answer against the question and expected key points.
     *
     * @param questionText      the interview question
     * @param expectedKeyPoints JSON array of expected points
     * @param answerText        candidate's answer
     * @return parsed evaluation with score, feedback, rubric
     */
    InterviewDTOs.LlmEvaluation evaluateAnswer(
            String questionText,
            String expectedKeyPoints,
            String answerText
    );

    /**
     * Generate a natural-language summary for the final report.
     *
     * @param jobTitle    role title
     * @param avgScore    overall average score
     * @param evaluations list of per-question feedback strings
     * @return markdown-formatted summary paragraph
     */
    String generateReportSummary(
            String jobTitle,
            double avgScore,
            List<String> evaluations
    );
}
