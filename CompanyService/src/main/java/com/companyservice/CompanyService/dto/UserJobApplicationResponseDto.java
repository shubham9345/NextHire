package com.companyservice.CompanyService.dto;

import com.companyservice.CompanyService.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class UserJobApplicationResponseDto {
    private UUID applicationId;
    private UUID jobId;
    private String jobTitle;
    private String jobDesc;
    private Instant appliedAt;
    private Double atsScore;
    private ApplicationStatus applicationStatus;
}
