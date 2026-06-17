package com.companyservice.CompanyService.service;

import com.companyservice.CompanyService.dto.CreateJobRequest;
import com.companyservice.CompanyService.dto.JobResponseDto;
import com.companyservice.CompanyService.dto.JobApplicationsRequest;
import com.companyservice.CompanyService.entity.Company;
import com.companyservice.CompanyService.entity.Job;
import com.companyservice.CompanyService.entity.JobStatus;
import com.companyservice.CompanyService.repository.CompanyRepository;
import com.companyservice.CompanyService.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final JobSyncService jobSyncService;

    private final CompanyRepository companyRepository;

    public JobResponseDto createJob(
            UUID companyId,
            CreateJobRequest request
    ) {

        Company company =
                companyRepository
                        .findByIdAndDeletedFalse(
                                companyId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Company not found"
                                )
                        );

        Job job = Job.builder()
                .company(company)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .jobType(request.getJobType())
                .experienceLevel(request.getExperienceLevel())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .maxCandidates(request.getMaxCandidates())
                .appliedCandidates(0)
                .status(JobStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        Job savedJob =
                jobRepository.save(job);
        jobSyncService.syncJob(job);

        log.info(
                "Job created successfully {} for company {}",
                savedJob.getId(),
                companyId
        );

        return mapToDto(savedJob);
    }

    public void deleteJob(
            UUID jobId
    ) {

        Job job =
                getActiveJob(jobId);

        job.setDeleted(true);

        jobRepository.save(job);
        jobSyncService.syncJob(job);

        log.info(
                "Job deleted successfully {}",
                jobId
        );
    }

    public JobResponseDto stopJob(
            UUID jobId
    ) {

        Job job =
                getActiveJob(jobId);

        stop(job);

        Job savedJob =
                jobRepository.save(job);
        jobSyncService.syncJob(job);

        log.info(
                "Job stopped successfully {}",
                jobId
        );

        return mapToDto(savedJob);
    }

    public JobResponseDto updateAppliedCandidates(
            UUID jobId,
            JobApplicationsRequest request
    ) {

        Job job =
                getActiveJob(jobId);

        job.setAppliedCandidates(
                request.getAppliedCandidates()
        );

        if (job.getAppliedCandidates() >= job.getMaxCandidates()) {
            stop(job);
        }

        Job savedJob =
                jobRepository.save(job);

        return mapToDto(savedJob);
    }

    public JobResponseDto getJob(
            UUID jobId
    ) {

        return mapToDto(
                getActiveJob(jobId)
        );
    }

    private Job getActiveJob(
            UUID jobId
    ) {

        return jobRepository
                .findByIdAndDeletedFalse(
                        jobId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Job not found"
                        )
                );
    }

    private void stop(
            Job job
    ) {

        if (job.getStatus() == JobStatus.CLOSED) {
            return;
        }

        job.setStatus(JobStatus.CLOSED);
        job.setStoppedAt(LocalDateTime.now());
    }

    private JobResponseDto mapToDto(
            Job job
    ) {

        return JobResponseDto.builder()
                .id(job.getId())
                .companyId(job.getCompany().getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .experienceLevel(job.getExperienceLevel())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .maxCandidates(job.getMaxCandidates())
                .appliedCandidates(job.getAppliedCandidates())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .stoppedAt(job.getStoppedAt())
                .build();
    }
    public Page<JobResponseDto> getJobsByCompany(UUID companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = jobRepository.findByCompanyIdWithCompany(companyId, pageable);

        return jobs.map(job -> JobResponseDto.builder()
                .id(job.getId())
                .companyId(job.getCompany().getId())  // lazy load triggers here
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .experienceLevel(job.getExperienceLevel())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .maxCandidates(job.getMaxCandidates())
                .appliedCandidates(job.getAppliedCandidates())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .stoppedAt(job.getStoppedAt())
                .build());
    }
}
