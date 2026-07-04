package com.companyservice.CompanyService.controller;

import com.companyservice.CompanyService.dto.ApplyJobRequest;
import com.companyservice.CompanyService.dto.UserJobApplicationResponseDto;
import com.companyservice.CompanyService.dto.JobApplicationResponseDto;
import com.companyservice.CompanyService.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationServiceImpl;

    @PostMapping(
            value = "/apply/{jobId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public JobApplicationResponseDto applyJob(
            @PathVariable UUID jobId,
            @Valid @ModelAttribute ApplyJobRequest request
    ) {

        return jobApplicationServiceImpl.applyJob(
                jobId,
                request
        );
    }

    @GetMapping("/applications/{jobId}")
    public List<JobApplicationResponseDto> getApplicationsByJob(
            @PathVariable UUID jobId
    ) {

        return jobApplicationServiceImpl.getApplicationsByJob(
                jobId
        );
    }
    @GetMapping("/applied-jobs")
    public ResponseEntity<Page<UserJobApplicationResponseDto>> getAppliedJobs(
            @RequestHeader("X-Auth-User-Id") UUID authUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserJobApplicationResponseDto> response = jobApplicationServiceImpl.getAppliedJobs(authUserId, page, size);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/shortlisted/{applicationId}")
    public ResponseEntity<String> shortlistCandidate(
            @PathVariable UUID applicationId) {

        jobApplicationServiceImpl.shortlistCandidate(applicationId);

        return ResponseEntity.ok("Candidate shortlisted successfully.");
    }
}
