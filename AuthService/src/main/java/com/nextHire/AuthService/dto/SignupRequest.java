package com.nextHire.AuthService.dto;

import com.nextHire.AuthService.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupRequest {

    @Email
    private String email;

    @NotBlank
    private String password;

    private Role role;
}