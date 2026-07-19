package com.companyservice.CompanyService.service;

import com.companyservice.CompanyService.dto.CreateJobRequest;
import com.companyservice.CompanyService.dto.JobApplicationsRequest;
import com.companyservice.CompanyService.dto.JobResponseDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface JobService {

    JobResponseDto createJob(
            UUID companyId,
            CreateJobRequest request
    );

    void deleteJob(
            UUID jobId
    );

    void stopJob(
            UUID jobId
    );

    JobResponseDto updateAppliedCandidates(
            UUID jobId,
            JobApplicationsRequest request
    );

    JobResponseDto getJob(
            UUID jobId
    );

    Page<JobResponseDto> getJobsByCompany(
            UUID companyId,
            int page,
            int size
    );
}