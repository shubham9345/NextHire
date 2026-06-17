package com.companyservice.CompanyService.service;

import com.companyservice.CompanyService.dto.ApplyJobRequest;
import com.companyservice.CompanyService.dto.AuthUserJobApplicationResponseDto;
import com.companyservice.CompanyService.dto.JobApplicationResponseDto;
import com.companyservice.CompanyService.entity.ApplicationStatus;
import com.companyservice.CompanyService.entity.Job;
import com.companyservice.CompanyService.entity.JobApplication;
import com.companyservice.CompanyService.entity.JobStatus;
import com.companyservice.CompanyService.repository.JobApplicationRepository;
import com.companyservice.CompanyService.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobApplicationService {

    private final JobRepository jobRepository;

    private final JobApplicationRepository jobApplicationRepository;

    private final ApplicationResumeStorageService resumeStorageService;

    private final AtsScoreService atsScoreService;

    @Transactional
    public JobApplicationResponseDto applyJob(
            UUID jobId,
            ApplyJobRequest request
    ) {

        Job job =
                jobRepository
                        .findByIdAndDeletedFalse(
                                jobId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Job not found"
                                )
                        );

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new RuntimeException(
                    "Job is stopped and not accepting applications"
            );
        }

        boolean alreadyApplied =
                jobApplicationRepository
                        .existsByJobIdAndCandidateProfileIdAndDeletedFalse(
                                jobId,
                                request.getCandidateProfileId()
                        );

        if (alreadyApplied) {
            throw new RuntimeException(
                    "Candidate has already applied for this job"
            );
        }

        String resumeUrl =
                resumeStorageService.uploadResume(
                        request.getResume()
                );

        JobApplication application =
                JobApplication.builder()
                        .job(job)
                        .candidateProfileId(request.getCandidateProfileId())
                        .candidateAuthUserId(request.getCandidateAuthUserId())
                        .fullName(request.getFullName())
                        .email(request.getEmail())
                        .phoneNumber(request.getPhoneNumber())
                        .headline(request.getHeadline())
                        .location(request.getLocation())
                        .yearsOfExperience(request.getYearsOfExperience())
                        .skills(request.getSkills())
                        .resumeUrl(resumeUrl)
                        .linkedinUrl(request.getLinkedinUrl())
                        .githubUrl(request.getGithubUrl())
                        .portfolioUrl(request.getPortfolioUrl())
                        .coverLetter(request.getCoverLetter())
                        .status(ApplicationStatus.APPLIED)
                        .appliedAt(LocalDateTime.now())
                        .deleted(false)
                        .atsScore(0.0)
                        .build();

        JobApplication savedApplication =
                jobApplicationRepository.save(application);
        JobApplication jobApplication = jobApplicationRepository.findByCandidateAuthUserId(savedApplication.getCandidateAuthUserId());

        long appliedCandidates =
                jobApplicationRepository
                        .countByJobIdAndDeletedFalse(
                                jobId
                        );

        job.setAppliedCandidates(
                Math.toIntExact(appliedCandidates)
        );

        if (appliedCandidates >= job.getMaxCandidates()) {
            job.setStatus(JobStatus.CLOSED);
            job.setStoppedAt(LocalDateTime.now());
        }

        jobRepository.save(job);

        log.info(
                "Candidate {} applied to job {}",
                request.getCandidateProfileId(),
                jobId
        );
        //  trigger ATS score calculation in background — non blocking
        atsScoreService.calculateAndUpdateAtsScore(
                jobApplication.getId(),
                request.getResume(),
                job.getDescription()   // job description as JD
        );

        return mapToDto(savedApplication);
    }

    public List<JobApplicationResponseDto> getApplicationsByJob(
            UUID jobId
    ) {

        Job job =
                jobRepository
                        .findByIdAndDeletedFalse(
                                jobId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Job not found"
                                )
                        );

        return jobApplicationRepository
                .findByJobIdAndDeletedFalseOrderByAtsScoreDesc(
                        job.getId()
                )
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private JobApplicationResponseDto mapToDto(
            JobApplication application
    ) {

        return JobApplicationResponseDto.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .companyId(application.getJob().getCompany().getId())
                .candidateProfileId(application.getCandidateProfileId())
                .candidateAuthUserId(application.getCandidateAuthUserId())
                .fullName(application.getFullName())
                .email(application.getEmail())
                .phoneNumber(application.getPhoneNumber())
                .headline(application.getHeadline())
                .location(application.getLocation())
                .yearsOfExperience(application.getYearsOfExperience())
                .skills(application.getSkills())
                .resumeUrl(application.getResumeUrl())
                .linkedinUrl(application.getLinkedinUrl())
                .githubUrl(application.getGithubUrl())
                .portfolioUrl(application.getPortfolioUrl())
                .coverLetter(application.getCoverLetter())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }
    public Page<AuthUserJobApplicationResponseDto> getAppliedJobs(UUID authUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobApplication> applications = jobApplicationRepository
                .findAppliedJobsWithJob(authUserId, pageable);

        return applications.map(app -> AuthUserJobApplicationResponseDto.builder()
                .jobId(app.getJob().getId())
                .jobTitle(app.getJob().getTitle())
                .jobDesc(app.getJob().getDescription())
                .appliedAt(app.getAppliedAt())
                .applicationStatus(app.getStatus())
                .build());
    }
}
