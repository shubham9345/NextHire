package com.companyservice.CompanyService.controller;

import com.companyservice.CompanyService.dto.CreateJobRequest;
import com.companyservice.CompanyService.dto.JobResponseDto;
import com.companyservice.CompanyService.dto.JobSearchFilterRequest;
import com.companyservice.CompanyService.dto.JobApplicationsRequest;
import com.companyservice.CompanyService.entity.JobType;
import com.companyservice.CompanyService.service.JobService;
import com.companyservice.CompanyService.serviceImpl.JobSearchServiceImpl;
import com.companyservice.CompanyService.serviceImpl.JobServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobServiceImpl;
    private final JobSearchServiceImpl jobSearchServiceImpl;

    @PostMapping("/job/{companyId}")
    public JobResponseDto createJob(
            @PathVariable UUID companyId,
            @Valid @RequestBody CreateJobRequest request
    ) {

        return jobServiceImpl.createJob(
                companyId,
                request
        );
    }

    @GetMapping("/{jobId}")
    public JobResponseDto getJob(
            @PathVariable UUID jobId
    ) {

        return jobServiceImpl.getJob(
                jobId
        );
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<String> deleteJob(
            @PathVariable UUID jobId
    ) {

        jobServiceImpl.deleteJob(
                jobId
        );

        return ResponseEntity.ok(
                "Job deleted successfully"
        );
    }

    @PatchMapping("/{jobId}/stop")
    public ResponseEntity<String> stopJob(
            @PathVariable UUID jobId
    ) {

        jobServiceImpl.stopJob(jobId);

        return ResponseEntity.ok("Job stopped successfully");
    }

    @PatchMapping("/{jobId}/applications")
    public JobResponseDto updateAppliedCandidates(
            @PathVariable UUID jobId,
            @Valid @RequestBody JobApplicationsRequest request
    ) {

        return jobServiceImpl.updateAppliedCandidates(
                jobId,
                request
        );
    }
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Page<JobResponseDto>> getJobsByCompany(
            @PathVariable UUID companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<JobResponseDto> response = jobServiceImpl.getJobsByCompany(companyId, page, size);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<JobResponseDto>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime postedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime postedTo,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        JobSearchFilterRequest filter = new JobSearchFilterRequest();
        filter.setTitle(title);
        filter.setLocation(location);
        filter.setPostedFrom(postedFrom);
        filter.setPostedTo(postedTo);
        filter.setJobType(jobType);
        filter.setExperienceLevel(experienceLevel);
        filter.setPage(page);
        filter.setSize(size);

        return ResponseEntity.ok(jobSearchServiceImpl.searchJobs(filter));
    }
}
