package com.UserService.UserService.service;

import com.UserService.UserService.dto.CreateProfileRequest;
import com.UserService.UserService.entity.UserProfile;
import com.UserService.UserService.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
            UUID profileId,
            String resumePath
    ) {

        UserProfile profile =
                repository.findById(profileId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Profile not found"
                                ));

        profile.setResumeUrl(resumePath);

        repository.save(profile);

        log.info(
                "Resume updated for profile={}",
                profileId
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
}
