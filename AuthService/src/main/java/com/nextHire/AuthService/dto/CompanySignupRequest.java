package com.nextHire.AuthService.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanySignupRequest {
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String companyName;
}