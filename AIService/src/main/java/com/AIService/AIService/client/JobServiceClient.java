package com.AIService.AIService.client;


import com.AIService.AIService.dto.InterviewDTOs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

/**
 * Feign client to fetch job details from JobService.
 * Name matches the Eureka registration of JobService.
 */
@FeignClient(name = "COMPANYSERVICE", fallback = JobServiceFallback.class)
public interface JobServiceClient{

    @GetMapping("/api/companies/{jobId}")
    InterviewDTOs.JobDetailDTO getJobById(
            @PathVariable("jobId") UUID jobId,
            @RequestHeader("Authorization") String bearerToken
    );
}