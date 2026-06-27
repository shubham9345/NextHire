package com.UserService.UserService.service;

import com.UserService.UserService.dto.CreateProfileRequest;
import com.UserService.UserService.dto.UpdateUserProfileRequest;
import com.UserService.UserService.dto.UserProfileFilterRequest;
import com.UserService.UserService.dto.UserProfileResponseDto;
import com.UserService.UserService.entity.UserProfile;
import org.springframework.data.domain.Page;


import java.util.UUID;

public interface UserProfileService {

    UserProfile createProfile(
            UUID authUserId,
            CreateProfileRequest request
    );

    void updateResume(
            UUID authId,
            String resumePath
    );

    UserProfile getProfile(UUID authUserId);

    UserProfileResponseDto updateProfile(
            UUID authUserId,
            UpdateUserProfileRequest request
    );

    Page<UserProfileResponseDto> getFilteredProfiles(
            UserProfileFilterRequest filter
    );
}