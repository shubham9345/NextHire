package com.companyservice.CompanyService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    private UUID candidateAuthUserId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    private String phoneNumber;

    private String headline;

    private String location;

    private Integer yearsOfExperience;

    @Column(length = 2000)
    private String skills;

    @Column(nullable = false)
    private String resumeUrl;

    private String linkedinUrl;

    private String githubUrl;

    private String portfolioUrl;

    @Column
    private Double atsScore;

    @Column(length = 4000)
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;
    @CreationTimestamp
    private Instant appliedAt;

    private boolean deleted;
}
