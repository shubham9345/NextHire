package com.companyservice.CompanyService.dto;

import com.companyservice.CompanyService.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AuthUserJobApplicationResponseDto {
    private UUID jobId;
    private String jobTitle;
    private String jobDesc;
    private LocalDateTime appliedAt;
    private ApplicationStatus applicationStatus;
}
