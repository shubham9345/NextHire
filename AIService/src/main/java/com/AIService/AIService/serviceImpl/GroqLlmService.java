package com.AIService.AIService.serviceImpl;


import com.AIService.AIService.dto.InterviewDTOs;
import com.AIService.AIService.service.LlmService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroqLlmService implements LlmService {

    // Groq uses an OpenAI-compatible API — only the base URL and model differ
    private static final String GROQ_BASE_URL = "https://api.groq.com/openai/v1";

    @Value("${llm.groq.api-key}")
    private String apiKey;

    @Value("${llm.groq.model}")
    private String model;

    @Value("${llm.groq.max-tokens}")
    private int maxTokens;

    @Value("${llm.timeout-seconds}")
    private int timeoutSeconds;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    // ─── Question generation ──────────────────────────────────────────────────

    @Override
    public List<InterviewDTOs.LlmQuestion> generateQuestions(
            String jobTitle,
            String jobDescription,
            String requiredSkills,
            String userSkills,
            int questionCount) {

        log.info("Generating {} questions via Groq for role: {}", questionCount, jobTitle);

        String systemPrompt = """
                You are an expert technical interviewer with 15 years of experience.
                You generate precise, role-relevant interview questions in strict JSON format.
                Never include markdown, preamble, or explanation — only valid JSON.
                """;

        String userPrompt = """
                Generate exactly %d interview questions for this hiring scenario.
                
                ROLE        : %s
                JOB DESCRIPTION:
                %s
                
                REQUIRED SKILLS : %s
                CANDIDATE SKILLS: %s
                
                RULES:
                1. Mix types: TECHNICAL, BEHAVIORAL, SITUATIONAL, SYSTEM_DESIGN
                2. Difficulty spread: ~30%% EASY, ~50%% MEDIUM, ~20%% HARD
                3. Prioritise skill gaps between required and candidate skills for TECHNICAL questions
                4. Each question needs 3-5 concrete expectedKeyPoints
                5. Return ONLY a raw JSON array — no markdown fences, no explanation
                
                EXACT OUTPUT FORMAT:
                [
                  {
                    "questionText": "Explain how HashMap works internally in Java.",
                    "questionType": "TECHNICAL",
                    "difficulty": "MEDIUM",
                    "expectedKeyPoints": [
                      "Hash function and bucket array",
                      "Collision handling via chaining or open addressing",
                      "Load factor and rehashing",
                      "Java 8 treeification of buckets"
                    ]
                  }
                ]
                """.formatted(questionCount, jobTitle, jobDescription,
                requiredSkills, userSkills);

        String raw = callGroq(systemPrompt, userPrompt);
        return parseQuestions(raw);
    }

    private List<InterviewDTOs.LlmQuestion> parseQuestions(String raw) {
        try {
            String clean = stripFences(raw);
            return objectMapper.readValue(clean, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Failed to parse Groq question response:\n{}", raw, e);
            throw new RuntimeException("Groq returned unparseable questions. Raw: " + raw, e);
        }
    }

    // ─── Answer evaluation ────────────────────────────────────────────────────

    @Override
    public InterviewDTOs.LlmEvaluation evaluateAnswer(
            String questionText,
            String expectedKeyPoints,
            String answerText) {

        log.info("Evaluating answer via Groq for question: {}",
                questionText.length() > 60 ? questionText.substring(0, 60) + "..." : questionText);

        String systemPrompt = """
                You are a strict but fair interview evaluator.
                You score answers on a 0-10 scale using a rubric.
                You return only valid JSON — no markdown, no explanation.
                """;

        String userPrompt = """
                Evaluate the candidate's answer below.
                
                QUESTION:
                %s
                
                EXPECTED KEY POINTS (what a strong answer should cover):
                %s
                
                CANDIDATE'S ANSWER:
                %s
                
                SCORING RUBRIC (each out of 10):
                - accuracy  : factual correctness
                - depth     : technical/conceptual depth
                - clarity   : how well the candidate communicated
                - relevance : how well the answer addressed the question
                
                Return ONLY this JSON object (no markdown, no extra text):
                {
                  "score": <weighted average of rubric, 1 decimal place>,
                  "feedback": "<2-3 sentences of actionable feedback for the candidate>",
                  "rubric": {
                    "accuracy":  <0-10>,
                    "depth":     <0-10>,
                    "clarity":   <0-10>,
                    "relevance": <0-10>
                  }
                }
                """.formatted(questionText, expectedKeyPoints, answerText);

        String raw = callGroq(systemPrompt, userPrompt);
        return parseEvaluation(raw);
    }

    private InterviewDTOs.LlmEvaluation parseEvaluation(String raw) {
        try {
            String clean = stripFences(raw);
            return objectMapper.readValue(clean, InterviewDTOs.LlmEvaluation.class);
        } catch (Exception e) {
            log.error("Failed to parse Groq evaluation response:\n{}", raw, e);
            throw new RuntimeException("Groq returned unparseable evaluation. Raw: " + raw, e);
        }
    }

    // ─── Report summary ───────────────────────────────────────────────────────

    @Override
    public String generateReportSummary(String jobTitle, double avgScore,
                                        List<String> feedbacks) {
        log.info("Generating report summary via Groq. avgScore={}", avgScore);

        String systemPrompt = """
                You are a senior HR analyst writing professional interview assessment reports.
                Write in clear, concise business English. No markdown headers or bullet points.
                """;

        String userPrompt = """
                Write a 3-paragraph interview performance summary.
                
                ROLE: %s
                OVERALL SCORE: %.1f / 10
                
                PER-QUESTION FEEDBACK COLLECTED:
                %s
                
                Paragraph 1 — Overall impression and score interpretation
                Paragraph 2 — Key strengths demonstrated
                Paragraph 3 — Improvement areas and hiring recommendation
                
                Be direct, professional, and constructive. Plain text only.
                """.formatted(jobTitle, avgScore,
                String.join("\n• ", feedbacks));

        return callGroq(systemPrompt, userPrompt);
    }

    // ─── Core HTTP call (Groq OpenAI-compatible endpoint) ────────────────────

    private String callGroq(String systemPrompt, String userPrompt) {
        WebClient client = webClientBuilder
                .baseUrl(GROQ_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "temperature", 0.4,          // lower = more deterministic/JSON-safe
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        try {
            Map<?, ?> response = client.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();

            if (response == null || !response.containsKey("choices")) {
                throw new RuntimeException("Groq returned null or missing 'choices' field");
            }

            List<?> choices = (List<?>) response.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("Groq returned empty choices array");
            }

            Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
            String content = (String) message.get("content");

            log.debug("Groq raw response: {}", content);
            return content;

        } catch (WebClientResponseException e) {
            log.error("Groq API HTTP error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Groq API error: " + e.getStatusCode(), e);
        }
    }

    // ─── Utility ──────────────────────────────────────────────────────────────

    private String stripFences(String raw) {
        return raw.strip()
                .replaceAll("(?s)^```json\\s*", "")
                .replaceAll("(?s)^```\\s*", "")
                .replaceAll("(?s)```\\s*$", "")
                .strip();
    }
}
