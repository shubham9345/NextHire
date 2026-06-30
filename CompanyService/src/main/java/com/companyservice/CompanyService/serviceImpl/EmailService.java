package com.companyservice.CompanyService.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendUpdateMail(String toEmail,
                               String subject,
                               String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        log.info(" email send successfully to {}", toEmail);
    }
    @Async
    public void sendShortlistedEmail(String candidateEmail) {
        String subject = "Congratulations! You've Been Shortlisted";

        String body = """
            Dear Candidate,

            Congratulations!

            We are pleased to inform you that you have been shortlisted for the next stage of our hiring process.

            As the next step, you are required to complete an AI-powered interview. This interview is designed to assess your skills and help us better understand your qualifications.

            Please visit our website to review the interview guidelines, important instructions, and complete your AI interview before the specified deadline.

            We recommend taking the interview in a quiet environment with a stable internet connection and ensuring your camera and microphone are working properly.

            We wish you the very best and look forward to your participation.

            Best regards,
            NextHire Management Team
            """;

        sendUpdateMail(candidateEmail, subject, body);
    }
}
