package com.companyservice.CompanyService.serviceImpl;

import com.companyservice.CompanyService.client.AtsClient;
import com.companyservice.CompanyService.entity.JobApplication;
import com.companyservice.CompanyService.repository.JobApplicationRepository;
import com.companyservice.CompanyService.service.AtsScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtsScoreServiceImpl implements AtsScoreService {

    private final JobApplicationRepository jobApplicationRepository;
    private final AtsClient atsClient;

    @Async("atsExecutor")
    public void calculateAndUpdateAtsScore(UUID applicationId,
                                           MultipartFile resume,
                                           String jobDescription) {
        try {
            Double score = atsClient.getAtsScore(resume, jobDescription);

            if (score != null) {
                JobApplication application = jobApplicationRepository
                        .findById(applicationId)
                        .orElseThrow();
                application.setAtsScore(score);
                jobApplicationRepository.save(application);
                System.out.println("ATS score updated for application: "
                        + applicationId + " score: " + score);
            }
        } catch (Exception e) {
            System.err.println("Failed to calculate ATS score for: " + applicationId);
        }
    }
}
