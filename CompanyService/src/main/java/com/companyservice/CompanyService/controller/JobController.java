package com.companyservice.CompanyService.controller;

import com.companyservice.CompanyService.dto.CreateJobRequest;
import com.companyservice.CompanyService.dto.JobResponseDto;
import com.companyservice.CompanyService.dto.UpdateJobApplicationsRequest;
import com.companyservice.CompanyService.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/job/{companyId}")
    public JobResponseDto createJob(
            @PathVariable UUID companyId,
            @Valid @RequestBody CreateJobRequest request
    ) {

        return jobService.createJob(
                companyId,
                request
        );
    }

    @GetMapping("/{jobId}")
    public JobResponseDto getJob(
            @PathVariable UUID jobId
    ) {

        return jobService.getJob(
                jobId
        );
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<String> deleteJob(
            @PathVariable UUID jobId
    ) {

        jobService.deleteJob(
                jobId
        );

        return ResponseEntity.ok(
                "Job deleted successfully"
        );
    }

    @PatchMapping("/{jobId}/stop")
    public JobResponseDto stopJob(
            @PathVariable UUID jobId
    ) {

        return jobService.stopJob(
                jobId
        );
    }

    @PatchMapping("/{jobId}/applications")
    public JobResponseDto updateAppliedCandidates(
            @PathVariable UUID jobId,
            @Valid @RequestBody UpdateJobApplicationsRequest request
    ) {

        return jobService.updateAppliedCandidates(
                jobId,
                request
        );
    }
}
