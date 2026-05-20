package com.nextHire.AuthService.service;

import com.nextHire.AuthService.dto.*;
import com.nextHire.AuthService.entity.RefreshToken;
import com.nextHire.AuthService.entity.User;
import com.nextHire.AuthService.enums.Role;
import com.nextHire.AuthService.repository.UserRepository;
import com.nextHire.AuthService.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @Autowired
    private final RefreshTokenService  refreshTokenService;

    public void signup(SignupRequest request) {

        log.info("Signup attempt for email={}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Email already exists: {}", request.getEmail());
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .createdAt(LocalDateTime.now())
                .isVerified(false)
                .build();

        userRepository.save(user);

        log.info("User registered successfully with email={}", request.getEmail());
    }

    public AuthResponse login(LoginRequest request) {

        log.info("Login attempt for email={}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.error("Invalid password for email={}", request.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateToken(user.getEmail());

        log.info("User logged in successfully email={}", user.getEmail());

        String refreshToken =
                jwtUtil.generateRefreshToken(user.getEmail());

        refreshTokenService.createRefreshToken(
                user,
                refreshToken
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void companySignup(
            CompanySignupRequest request
    ) {

        if (
                userRepository.existsByEmail(
                        request.getEmail()
                )
        ) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }

        User company = User.builder()
                .email(request.getEmail())
                .passwordHash(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(Role.COMPANY)
                .isVerified(false)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(company);

        log.info(
                "Company registered successfully"
        );
    }

    public AuthResponse refreshToken(
            RefreshTokenRequest request
    ) {

        RefreshToken refreshToken =
                refreshTokenService.verifyRefreshToken(
                        request.getRefreshToken()
                );

        User user = refreshToken.getUser();

        String newAccessToken =
                jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
