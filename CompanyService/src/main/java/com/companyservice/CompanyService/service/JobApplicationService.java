package com.companyservice.CompanyService.service;

import com.companyservice.CompanyService.dto.ApplyJobRequest;
import com.companyservice.CompanyService.dto.UserJobApplicationResponseDto;
import com.companyservice.CompanyService.dto.JobApplicationResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface JobApplicationService {

    JobApplicationResponseDto applyJob(
            UUID jobId,
            ApplyJobRequest request
    );

    List<JobApplicationResponseDto> getApplicationsByJob(
            UUID jobId
    );

    Page<UserJobApplicationResponseDto> getAppliedJobs(
            UUID authUserId,
            int page,
            int size
    );
    void shortlistCandidate(UUID applicationId);
}