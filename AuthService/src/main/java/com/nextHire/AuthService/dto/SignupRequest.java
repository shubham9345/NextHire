package com.nextHire.AuthService.dto;

import com.nextHire.AuthService.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupRequest {

    @Email(message = "enter the valid emailId")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email must contain a valid domain"
    )
    private String email;
    @NotBlank(message = "username can not be empty")
    private String username;
    @NotBlank(message = "password must be greater than 8 digit")
    private String password;
    private Role roles;
    private String companyName;
}