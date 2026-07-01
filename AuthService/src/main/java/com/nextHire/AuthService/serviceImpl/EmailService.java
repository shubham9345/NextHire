package com.nextHire.AuthService.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("NextHire - Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp +
                "\nThis code expires in 5 minutes. If you didn't request this, please ignore.");
        mailSender.send(message);
    }
}
