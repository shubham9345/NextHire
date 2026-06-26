package com.AIService.AIService.serviceImpl;

import com.AIService.AIService.dto.ATSResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtsService {

    private final ChatClient.Builder chatClientBuilder;

    private final ResumeParserService parserService;

    public ATSResponseDto analyzeResume(
            MultipartFile resume,
            String jobDescription
    ) {

        try {

            String resumeText =
                    parserService.extractText(
                            resume
                    );

            String prompt =
                    buildPrompt(
                            jobDescription,
                            resumeText
                    );

            ChatClient chatClient =
                    chatClientBuilder.build();

            ATSResponseDto response =
                    chatClient.prompt()
                            .user(prompt)
                            .call()
                            .entity(ATSResponseDto.class);

            log.info(
                    "ATS analysis completed"
            );

            return response;

        } catch (Exception e) {

            log.error(
                    "ATS analysis failed",
                    e
            );

            throw new RuntimeException(
                    "ATS analysis failed"
            );
        }
    }

    private String buildPrompt(
            String jd,
            String resume
    ) {

        return """
            You are an ATS analyzer.

            Return ONLY valid JSON.

            Required JSON structure:

            {
              "atsScore": number,
              "matchedSkills": [],
              "missingSkills": [],
              "profileSummary": "",
              "strengths": [],
              "weaknesses": [],
              "improvementSuggestions": [],
              "recommendedRole": "",
              "experienceLevel": ""
            }

            JOB DESCRIPTION:
            %s

            RESUME:
            %s
            """.formatted(jd, resume);
    }
}