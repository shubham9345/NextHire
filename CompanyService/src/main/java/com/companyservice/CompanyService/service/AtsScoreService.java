package com.companyservice.CompanyService.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AtsScoreService {

    void calculateAndUpdateAtsScore(
            UUID applicationId,
            MultipartFile resume,
            String jobDescription
    );
}
