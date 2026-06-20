package com.nextHire.AuthService.client;

import com.nextHire.AuthService.dto.CompanyProfileRequest;
import com.nextHire.AuthService.dto.CompanyResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceClient {

    private final RestTemplate restTemplate;

    private static final String COMPANY_SERVICE_URL = "http://COMPANYSERVICE";

    public void createCompanyProfile(UUID authUserId, String companyName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            CompanyProfileRequest request = CompanyProfileRequest.builder()
                    .companyName(companyName)
                    .build();

            HttpEntity<CompanyProfileRequest> entity = new HttpEntity<>(request, headers);

            restTemplate.postForObject(
                    COMPANY_SERVICE_URL + "/api/companies/profile/" + authUserId,
                    entity,
                    CompanyResponseDto.class
            );

            log.info("Company profile created for authUserId: {}", authUserId);

        } catch (Exception e) {
            log.error("Failed to create company profile for authUserId: {}", authUserId, e);
        }
    }
}
