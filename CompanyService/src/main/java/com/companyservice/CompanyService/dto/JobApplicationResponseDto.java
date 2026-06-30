package com.companyservice.CompanyService.dto;

import com.companyservice.CompanyService.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class JobApplicationResponseDto {

    private UUID id;

    private UUID jobId;

    private UUID companyId;

    private UUID candidateProfileId;

    private UUID candidateAuthUserId;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String headline;

    private String location;

    private Integer yearsOfExperience;

    private String skills;

    private double atsScore;

    private String resumeUrl;

    private String linkedinUrl;

    private String githubUrl;

    private String portfolioUrl;

    private String coverLetter;

    private ApplicationStatus status;

    private Instant appliedAt;
}
