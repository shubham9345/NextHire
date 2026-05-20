package com.nextHire.AuthService.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
