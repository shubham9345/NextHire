package com.UserService.UserService.controller;

import com.UserService.UserService.dto.CreateProfileRequest;
import com.UserService.UserService.dto.UpdateUserProfileRequest;
import com.UserService.UserService.dto.UserProfileFilterRequest;
import com.UserService.UserService.dto.UserProfileResponseDto;
import com.UserService.UserService.entity.UserProfile;
import com.UserService.UserService.service.FileStorageService;
import com.UserService.UserService.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final FileStorageService fileStorageService;

    @PostMapping("/profile/{authUserId}")
    public UserProfile createProfile(
            @PathVariable UUID authUserId,
            @Valid @RequestBody CreateProfileRequest request
    ) {

        return userProfileService.createProfile(authUserId, request);
    }
    @PostMapping(
            value = "/resume/{authId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadResume(
            @PathVariable UUID authId,
            @RequestParam("file") MultipartFile file
    ) {

        String path =
                fileStorageService.uploadResume(file);

        userProfileService.updateResume(
                authId,
                path
        );

        return ResponseEntity.ok(
                "Resume uploaded successfully"
        );
    }
    @GetMapping("/profile/{authId}")
    public ResponseEntity<UserProfile> getProfile(
            @PathVariable UUID authId
    ) {

        return ResponseEntity.ok(
                userProfileService.getProfile(authId)
        );
    }

    @PutMapping("/profile/{authUserId}")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @PathVariable UUID authUserId,
            @RequestBody UpdateUserProfileRequest request
    ) {
        UserProfileResponseDto response = userProfileService.updateProfile(authUserId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profiles")
    public ResponseEntity<Page<UserProfileResponseDto>> getFilteredProfiles(
            @RequestParam(required = false) Integer yearsOfExperience,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String headline,
            @RequestParam(required = false) String skills,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UserProfileFilterRequest filter = new UserProfileFilterRequest();
        filter.setYearsOfExperience(yearsOfExperience);
        filter.setLocation(location);
        filter.setHeadline(headline);
        filter.setSkills(skills);
        filter.setPage(page);
        filter.setSize(size);

        Page<UserProfileResponseDto> response = userProfileService.getFilteredProfiles(filter);
        return ResponseEntity.ok(response);
    }
}

