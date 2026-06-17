package com.companyservice.CompanyService.client;

import com.companyservice.CompanyService.dto.ATSResponseDto;
import com.companyservice.CompanyService.utils.MultipartInputStreamFileResource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AtsClient {

    private final RestTemplate restTemplate;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public Double getAtsScore(MultipartFile resume, String jobDescription) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("resume", new MultipartInputStreamFileResource(
                    resume.getInputStream(),
                    resume.getOriginalFilename()
            ));
            body.add("jobDescription", jobDescription);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ATSResponseDto response = restTemplate.postForObject(
                    aiServiceUrl + "/api/ai/analyze",
                    request,
                    ATSResponseDto.class
            );

            return response != null ? response.getAtsScore() : null;
        } catch (Exception e) {
            return null;
        }
    }
}