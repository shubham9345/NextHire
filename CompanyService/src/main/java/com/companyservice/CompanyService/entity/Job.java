package com.companyservice.CompanyService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    private String location;

    private String jobType;

    private String experienceLevel;

    private Integer salaryMin;

    private Integer salaryMax;

    @Column(nullable = false)
    private Integer maxCandidates;

    @Column(nullable = false)
    private Integer appliedCandidates;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime stoppedAt;

    private boolean deleted;
}
