package com.UserService.UserService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProfileRequest {

    @NotBlank
    private String fullName;

    private String phoneNumber;

    private String headline;

    private String bio;

    private String location;

    private Integer yearsOfExperience;

    private String skills;
}