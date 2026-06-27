package com.nextHire.AuthService.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.nextHire.AuthService.entity.UserInfo;
import com.nextHire.AuthService.enums.Role;
import com.nextHire.AuthService.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class GoogleAuthService {

    @Value("${google.client-id}")
    private String googleClientId;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserInfo verifyAndGetUser(String idToken) throws Exception {
        // 1. Build the verifier
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        // 2. Verify the token
        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) {
            throw new RuntimeException("Invalid Google ID token");
        }

        // 3. Extract payload
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String googleId = payload.getSubject(); // unique Google user ID

        // 4. Find or create user
        UserInfo existingUser = userInfoRepository.findByUsername(email);
        if (existingUser != null) {
            return existingUser; // already registered, just return
        }

        // 5. Auto-register new Google user
        UserInfo newUser = new UserInfo();
        newUser.setUsername(email);
        newUser.setUsername(name);
        newUser.setGoogleId(googleId);
        newUser.setProvider("GOOGLE");
        // Random password — Google users will never use password login
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setRoles(Role.CANDIDATE); // default role

        return userInfoRepository.save(newUser);
    }
}