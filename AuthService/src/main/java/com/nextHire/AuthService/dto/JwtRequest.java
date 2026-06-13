package com.nextHire.AuthService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class JwtRequest {
    @NotBlank(message = "username cannot be empty")
    private String username;
    @NotBlank(message = "password cannot be blank")
    private String password;
}