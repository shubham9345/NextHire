package com.nextHire.AuthService.service;

import com.nextHire.AuthService.client.UserServiceClient;
import com.nextHire.AuthService.dto.SignupRequest;
import com.nextHire.AuthService.entity.UserInfo;
import com.nextHire.AuthService.enums.Role;
import com.nextHire.AuthService.exception.UserNotFoundException;
import com.nextHire.AuthService.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;


    public UserInfo AddUser(SignupRequest signupRequest) {
        UserInfo existingUser = userInfoRepository.findByUsername(signupRequest.getUsername());
        UserInfo existingUserByEmail = userInfoRepository.findByEmail(signupRequest.getEmail());

        if (existingUser != null || existingUserByEmail != null) {
            throw new RuntimeException("username or email already exist!!");
        }

        UserInfo user = new UserInfo();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setRoles(signupRequest.getRoles());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        UserInfo saved = userInfoRepository.save(user);

        // call user service to create profile after saving user
        if (signupRequest.getRoles() == Role.CANDIDATE) {
            userServiceClient.createUserProfile(
                    saved.getId(),
                    saved.getUsername(),
                    saved.getEmail()
            );
        }

        return saved;
    }
    public UserInfo getUserbyId(UUID Id) {
        Optional<UserInfo> UserOptional = userInfoRepository.findById(Id);
        if (UserOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with Id" + Id,"user is not found!! check it once");
        }
        return UserOptional.get();
    }

    public List<UserInfo> getAllUser() {
        return userInfoRepository.findAll();
    }
}
