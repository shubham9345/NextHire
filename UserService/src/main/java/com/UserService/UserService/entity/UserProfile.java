package com.UserService.UserService.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue
    private UUID id;

    // Reference from Auth Service
    @Column(nullable = false, unique = true)
    private UUID authUserId;

    @Column(nullable = false)
    private String fullName;

    private String phoneNumber;

    private String email;

    private String headline;

    @Column(length = 2000)
    private String bio;

    private String location;

    private Integer yearsOfExperience;

    private String skills;

    private String resumeUrl;

    private String linkedinUrl;

    private String githubUrl;

    private LocalDate dateOfBirth;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}