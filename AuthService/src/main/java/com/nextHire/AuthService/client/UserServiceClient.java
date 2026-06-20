package com.nextHire.AuthService.client;

import com.nextHire.AuthService.dto.CreateProfileRequest;
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
public class UserServiceClient {

    private final RestTemplate restTemplate;

    private static final String USER_SERVICE_URL = "http://USERSERVICE";

    public void createUserProfile(UUID authUserId, String fullName,String email) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            CreateProfileRequest request = CreateProfileRequest.builder()
                    .fullName(fullName)
                    .email(email)
                    .build();

            HttpEntity<CreateProfileRequest> entity = new HttpEntity<>(request, headers);

            restTemplate.postForObject(
                    USER_SERVICE_URL + "/api/users/profile/" + authUserId,
                    entity,
                    Object.class
            );

            log.info("User profile created for authUserId: {}", authUserId);

        } catch (Exception e) {
            log.error("Failed to create user profile for authUserId: {}", authUserId, e);
        }
    }
}