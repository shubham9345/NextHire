package com.AIService.AIService.client;


import com.AIService.AIService.dto.InterviewDTOs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

/**
 * Feign client to fetch user profile from UserService.
 */
@FeignClient(name = "USERSERVICE", fallback = UserServiceFallback.class)
public interface UserServiceClient {

    @GetMapping("/api/users/profile/{userId}")
    InterviewDTOs.UserProfileDTO getUserProfile(
            @PathVariable("userId") UUID userId,
            @RequestHeader("Authorization") String bearerToken
    );
}