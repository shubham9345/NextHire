package com.nextHire.AuthService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileRequest {
    private String fullName;
    private String phoneNumber;
    private String headline;
    private String bio;
    private String location;
    private Integer yearsOfExperience;
    private String skills;
    private String resumeUrl;
    private String linkedinUrl;
    private String githubUrl;
    private LocalDate dateOfBirth;
}
