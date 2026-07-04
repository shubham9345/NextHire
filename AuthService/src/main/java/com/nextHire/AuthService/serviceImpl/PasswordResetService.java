package com.nextHire.AuthService.serviceImpl;

import com.nextHire.AuthService.entity.PasswordResetOtp;
import com.nextHire.AuthService.entity.UserInfo;
import com.nextHire.AuthService.exception.UserNotFoundException;
import com.nextHire.AuthService.repository.PasswordResetOtpRepository;
import com.nextHire.AuthService.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserInfoRepository userInfoRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.otp.expiry-minutes}")
    private long otpExpiryMinutes;

    @Value("${app.reset-token.expiry-minutes}")
    private long resetTokenExpiryMinutes;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Transactional
    public void sendOtp(String email) {
        UserInfo user = userInfoRepository.findByEmail(email);
         if(user==null) throw new UserNotFoundException("No account found with this email","emailId not found");

        String otp = String.valueOf(100000 + RANDOM.nextInt(900000)); // 6-digit

        PasswordResetOtp entry = PasswordResetOtp.builder()
                .email(email)
                .otpHash(passwordEncoder.encode(otp))
                .otpExpiry(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .otpVerified(false)
                .createdAt(LocalDateTime.now())
                .build();

        otpRepository.save(entry);
        emailService.sendOtpEmail(email, otp);
    }

    @Transactional
    public String verifyOtp(String email, String otp) {
        PasswordResetOtp entry = otpRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("No OTP request found for this email"));

        if (entry.isOtpVerified()) {
            throw new RuntimeException("OTP already used. Please request a new one");
        }

        if (entry.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired. Please request a new one");
        }

        if (!passwordEncoder.matches(otp, entry.getOtpHash())) {
            throw new RuntimeException("Invalid OTP");
        }

        String resetToken = UUID.randomUUID().toString();
        entry.setOtpVerified(true);
        entry.setResetToken(resetToken);
        entry.setResetTokenExpiry(LocalDateTime.now().plusMinutes(resetTokenExpiryMinutes));
        otpRepository.save(entry);

        return resetToken;
    }

    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        PasswordResetOtp entry = otpRepository.findByResetToken(resetToken)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (!entry.isOtpVerified()) {
            throw new RuntimeException("OTP not verified for this request");
        }

        if (entry.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired. Please start again");
        }

        UserInfo user = userInfoRepository.findByEmail(entry.getEmail());
        if(user==null) throw new UserNotFoundException("No account found with this email","emailId not found");

        user.setPassword(passwordEncoder.encode(newPassword));
        userInfoRepository.save(user);

        entry.setResetToken(null);
        entry.setResetTokenExpiry(null);
        otpRepository.save(entry);
    }
}