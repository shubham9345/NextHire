package com.nextHire.AuthService.serviceImpl;

import com.nextHire.AuthService.client.CompanyServiceClient;
import com.nextHire.AuthService.client.UserServiceClient;
import com.nextHire.AuthService.dto.SignupRequest;
import com.nextHire.AuthService.entity.UserInfo;
import com.nextHire.AuthService.enums.Role;
import com.nextHire.AuthService.exception.UserAlreadyExistsException;
import com.nextHire.AuthService.exception.UserNotFoundException;
import com.nextHire.AuthService.repository.UserInfoRepository;
import com.nextHire.AuthService.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;
    private final CompanyServiceClient companyServiceClient;


    public UserInfo AddUser(SignupRequest signupRequest) {
        UserInfo existingUser = userInfoRepository.findByUsername(signupRequest.getUsername());
        UserInfo existingUserByEmail = userInfoRepository.findByEmail(signupRequest.getEmail());

        if (existingUser != null || existingUserByEmail != null) {
            throw new UserAlreadyExistsException("username or email already exist!!","username or email already exist!!");
        }

        UserInfo user = new UserInfo();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setRoles(signupRequest.getRoles());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        if(signupRequest.getRoles() == Role.COMPANY){
            if(signupRequest.getCompanyName() == null) throw new UserNotFoundException("company name should not be null","company name should not be null");
            else{
                user.setCompanyName(signupRequest.getCompanyName());
            }
        }

        UserInfo saved = userInfoRepository.save(user);

        // call user service to create profile after saving user
        if (signupRequest.getRoles() == Role.CANDIDATE) {
            userServiceClient.createUserProfile(
                    saved.getId(),
                    saved.getUsername(),
                    saved.getEmail()
            );
        } else if (signupRequest.getRoles() == Role.COMPANY) {
            companyServiceClient.createCompanyProfile(
                    saved.getId(),
                    saved.getCompanyName()
            );
        }

        return saved;
    }
    public UserInfo getUserbyId(UUID userId) {
        Optional<UserInfo> UserOptional = userInfoRepository.findById(userId);
        if (UserOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with Id" + userId,"user is not found!! check it once");
        }
        return UserOptional.get();
    }

    public List<UserInfo> getAllUser() {
        return userInfoRepository.findAll();
    }
}
