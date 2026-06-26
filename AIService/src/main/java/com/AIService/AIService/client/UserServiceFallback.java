package com.AIService.AIService.client;


import com.AIService.AIService.dto.InterviewDTOs;

import java.util.UUID;

class UserServiceFallback implements UserServiceClient {
    @Override
    public InterviewDTOs.UserProfileDTO getUserProfile(UUID userId, String bearerToken) {
        InterviewDTOs.UserProfileDTO stub = new InterviewDTOs.UserProfileDTO();
        stub.setId(userId);
        stub.setFullName("Candidate");
        stub.setSkills("");
        stub.setYearsOfExperience(0);
        return stub;
    }
}
