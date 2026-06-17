package com.companyservice.CompanyService.service;

import com.companyservice.CompanyService.entity.Job;
import com.companyservice.CompanyService.entity.JobDocument;
import com.companyservice.CompanyService.repository.JobSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSyncService {

    private final JobSearchRepository jobSearchRepository;

    public void syncJob(Job job) {
        JobDocument doc = JobDocument.builder()
                .id(job.getId().toString())
                .companyId(job.getCompany().getId().toString())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .experienceLevel(job.getExperienceLevel())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .maxCandidates(job.getMaxCandidates())
                .appliedCandidates(job.getAppliedCandidates())
                .status(job.getStatus().name())
                .createdAt(job.getCreatedAt())
                .stoppedAt(job.getStoppedAt())
                .build();

        jobSearchRepository.save(doc);
        log.info(
                "Job are successfully added in jobsearchdb"
        );
    }

    public void deleteJob(UUID jobId) {
        jobSearchRepository.deleteById(jobId.toString());
    }
}
