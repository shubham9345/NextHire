package com.nextHire.AuthService.controller;

import com.nextHire.AuthService.dto.*;
import com.nextHire.AuthService.service.AuthService;
import com.nextHire.AuthService.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final AuthService authService;
    @Autowired
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @Valid @RequestBody SignupRequest request
    ) {

        authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {

        return ResponseEntity.ok(
                authService.refreshToken(request)
        );
    }

    @PostMapping("/company/signup")
    public ResponseEntity<String> companySignup(
            @Valid
            @RequestBody
            CompanySignupRequest request
    ) {

        authService.companySignup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        "Company registered successfully"
                );
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestBody LogoutRequest request
    ) {

        refreshTokenService.revokeToken(
                request.getRefreshToken()
        );

        return ResponseEntity.ok("Logged out successfully");
    }
}
