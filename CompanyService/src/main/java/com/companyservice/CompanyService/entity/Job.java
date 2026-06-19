package com.companyservice.CompanyService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
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
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private Instant stoppedAt;

    private boolean deleted;
}
