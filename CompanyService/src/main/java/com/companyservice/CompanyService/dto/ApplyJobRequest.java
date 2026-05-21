package com.companyservice.CompanyService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
public class ApplyJobRequest {

    @NotNull
    private UUID candidateProfileId;

    private UUID candidateAuthUserId;

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    private String phoneNumber;

    private String headline;

    private String location;

    @Min(0)
    private Integer yearsOfExperience;

    private String skills;

    private String linkedinUrl;

    private String githubUrl;

    private String portfolioUrl;

    private String coverLetter;

    @NotNull
    private MultipartFile resume;
}
