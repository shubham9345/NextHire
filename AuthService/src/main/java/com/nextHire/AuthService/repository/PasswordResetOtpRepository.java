package com.nextHire.AuthService.repository;

import com.nextHire.AuthService.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, UUID> {

    // latest active row for an email (most recent request)
    Optional<PasswordResetOtp> findTopByEmailOrderByCreatedAtDesc(String email);

    Optional<PasswordResetOtp> findByResetToken(String resetToken);

    List<PasswordResetOtp> findAllByEmail(String email);

    List<PasswordResetOtp> findAllByOtpExpiryBeforeAndResetTokenExpiryBefore(
            LocalDateTime otpCutoff, LocalDateTime tokenCutoff);
}