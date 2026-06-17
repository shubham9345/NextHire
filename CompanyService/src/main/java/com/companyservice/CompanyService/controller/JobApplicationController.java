package com.companyservice.CompanyService.controller;

import com.companyservice.CompanyService.dto.ApplyJobRequest;
import com.companyservice.CompanyService.dto.AuthUserJobApplicationResponseDto;
import com.companyservice.CompanyService.dto.JobApplicationResponseDto;
import com.companyservice.CompanyService.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping(
            value = "/{jobId}/apply",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public JobApplicationResponseDto applyJob(
            @PathVariable UUID jobId,
            @Valid @ModelAttribute ApplyJobRequest request
    ) {

        return jobApplicationService.applyJob(
                jobId,
                request
        );
    }

    @GetMapping("/{jobId}/applications")
    public List<JobApplicationResponseDto> getApplicationsByJob(
            @PathVariable UUID jobId
    ) {

        return jobApplicationService.getApplicationsByJob(
                jobId
        );
    }
    @GetMapping("/applied-jobs")
    public ResponseEntity<Page<AuthUserJobApplicationResponseDto>> getAppliedJobs(
            @RequestHeader("X-Auth-User-Id") UUID authUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AuthUserJobApplicationResponseDto> response = jobApplicationService.getAppliedJobs(authUserId, page, size);
        return ResponseEntity.ok(response);
    }
}
