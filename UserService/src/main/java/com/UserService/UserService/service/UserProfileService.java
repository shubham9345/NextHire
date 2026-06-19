package com.UserService.UserService.service;

import com.UserService.UserService.dto.CreateProfileRequest;
import com.UserService.UserService.dto.UpdateUserProfileRequest;
import com.UserService.UserService.dto.UserProfileResponseDto;
import com.UserService.UserService.entity.UserProfile;
import com.UserService.UserService.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserProfileRepository repository;

    public UserProfile createProfile(
            UUID authUserId,
            CreateProfileRequest request
    ) {

        log.info("Creating profile for user={}", authUserId);

        UserProfile profile = UserProfile.builder()
                .authUserId(authUserId)
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .headline(request.getHeadline())
                .bio(request.getBio())
                .location(request.getLocation())
                .yearsOfExperience(request.getYearsOfExperience())
                .skills(request.getSkills())
                .linkedinUrl(request.getLinkedinUrl())
                .dateOfBirth(request.getDateOfBirth())
                .githubUrl(request.getGithubUrl())
                .build();

        return repository.save(profile);
    }

//    @CacheEvict(
//            value = "profiles",
//            key = "#profileId"
//    )
    public void updateResume(
            UUID authId,
            String resumePath
    ) {

        UserProfile profile =
                repository.findByAuthUserId(authId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Profile not found"
                                ));

        profile.setResumeUrl(resumePath);

        repository.save(profile);

        log.info(
                "Resume updated for profile={}",
                authId
        );
    }
//    @Cacheable(
//            value = "profiles",
//            key = "#authUserId"
//    )
    public UserProfile getProfile(UUID authUserId) {

        return repository.findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));
    }

    public UserProfileResponseDto updateProfile(UUID authUserId, UpdateUserProfileRequest request) {
        UserProfile profile = repository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        if (StringUtils.hasText(request.getFullName())) {
            profile.setFullName(request.getFullName());
        }
        if (StringUtils.hasText(request.getPhoneNumber())) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (StringUtils.hasText(request.getHeadline())) {
            profile.setHeadline(request.getHeadline());
        }
        if (StringUtils.hasText(request.getBio())) {
            profile.setBio(request.getBio());
        }
        if (StringUtils.hasText(request.getLocation())) {
            profile.setLocation(request.getLocation());
        }
        if (request.getYearsOfExperience() != null) {
            profile.setYearsOfExperience(request.getYearsOfExperience());
        }
        if (StringUtils.hasText(request.getSkills())) {
            profile.setSkills(request.getSkills());
        }
        if (StringUtils.hasText(request.getResumeUrl())) {
            profile.setResumeUrl(request.getResumeUrl());
        }
        if (StringUtils.hasText(request.getLinkedinUrl())) {
            profile.setLinkedinUrl(request.getLinkedinUrl());
        }
        if (StringUtils.hasText(request.getGithubUrl())) {
            profile.setGithubUrl(request.getGithubUrl());
        }
        if (request.getDateOfBirth() != null) {
            profile.setDateOfBirth(request.getDateOfBirth());
        }

        UserProfile updated = repository.save(profile);
        return mapToDto(updated);
    }

    private UserProfileResponseDto mapToDto(UserProfile profile) {
        return UserProfileResponseDto.builder()
                .id(profile.getId())
                .authUserId(profile.getAuthUserId())
                .fullName(profile.getFullName())
                .phoneNumber(profile.getPhoneNumber())
                .headline(profile.getHeadline())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .yearsOfExperience(profile.getYearsOfExperience())
                .skills(profile.getSkills())
                .resumeUrl(profile.getResumeUrl())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .dateOfBirth(profile.getDateOfBirth())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
