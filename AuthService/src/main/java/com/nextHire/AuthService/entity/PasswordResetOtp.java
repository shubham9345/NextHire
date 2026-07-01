package com.nextHire.AuthService.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_otp")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetOtp {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String email;

    // hashed OTP, never store plain text
    private String otpHash;

    private LocalDateTime otpExpiry;

    @Builder.Default
    private boolean otpVerified = false;

    // set only after OTP is verified; used for the final reset-password step
    private String resetToken;

    private LocalDateTime resetTokenExpiry;

    private LocalDateTime createdAt;
}