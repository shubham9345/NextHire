package com.UserService.UserService.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserProfileResponseDto {
    private UUID id;
    private UUID authUserId;
    private String fullName;
    private String phoneNumber;
    private String headline;
    private String bio;
    private String email;
    private String location;
    private Integer yearsOfExperience;
    private String skills;
    private String resumeUrl;
    private String linkedinUrl;
    private String githubUrl;
    private LocalDate dateOfBirth;
    private LocalDateTime createdAt;
}
