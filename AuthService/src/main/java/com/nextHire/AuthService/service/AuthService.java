package com.nextHire.AuthService.service;

import com.nextHire.AuthService.dto.SignupRequest;
import com.nextHire.AuthService.entity.UserInfo;

import java.util.List;
import java.util.UUID;

public interface AuthService {

    UserInfo AddUser(SignupRequest signupRequest);

    UserInfo getUserbyId(UUID userId);

    List<UserInfo> getAllUser();
}