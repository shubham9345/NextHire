package com.nextHire.AuthService.utils;

import com.nextHire.AuthService.repository.PasswordResetOtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PasswordResetCleanupScheduler {

    private final PasswordResetOtpRepository otpRepository;

    // runs every hour, deletes rows where both OTP and reset token are expired
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void cleanupExpiredEntries() {
        LocalDateTime now = LocalDateTime.now();
        var expired = otpRepository.findAllByOtpExpiryBeforeAndResetTokenExpiryBefore(now, now);
        otpRepository.deleteAll(expired);
    }
}