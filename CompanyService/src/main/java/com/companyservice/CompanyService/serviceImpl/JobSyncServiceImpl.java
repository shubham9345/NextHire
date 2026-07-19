package com.companyservice.CompanyService.serviceImpl;

import com.companyservice.CompanyService.entity.Job;
import com.companyservice.CompanyService.entity.JobDocument;
import com.companyservice.CompanyService.repository.JobSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSyncServiceImpl {

    private final JobSearchRepository jobSearchRepository;

    public void syncJob(Job job) {
        JobDocument doc = JobDocument.builder()
                .id(job.getId().toString())
                .companyId(job.getCompany().getId().toString())
                .companyName(job.getCompany().getCompanyName())
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
                .createdAt(LocalDateTime.now())
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
