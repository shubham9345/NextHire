package com.AIService.AIService.dto;

import lombok.Data;

import java.util.List;

@Data
public class ATSResponseDto {

    private double atsScore;

    private List<String> matchedSkills;

    private List<String> missingSkills;

    private String profileSummary;

    private List<String> strengths;

    private List<String> weaknesses;

    private List<String> improvementSuggestions;

    private String recommendedRole;

    private String experienceLevel;
}
