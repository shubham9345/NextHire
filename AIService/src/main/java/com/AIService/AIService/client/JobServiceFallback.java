package com.AIService.AIService.client;


import com.AIService.AIService.dto.InterviewDTOs;

import java.util.UUID;

class JobServiceFallback implements JobServiceClient {
    @Override
    public InterviewDTOs.JobDetailDTO getJobById(UUID jobId, String bearerToken) {
        // Return a minimal stub so session creation can still proceed
        InterviewDTOs.JobDetailDTO stub = new InterviewDTOs.JobDetailDTO();
        stub.setId(jobId);
        stub.setTitle("Unknown Job");
        stub.setDescription("");
        stub.setRequiredSkills("");
        return stub;
    }
}
