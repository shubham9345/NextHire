package com.companyservice.CompanyService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID authUserId;

    @Column(nullable = false)
    private String companyName;

    private String website;

    private String industry;
    @Column(length = 5000)
    private String description;

    private String location;

    private String logoUrl;

    private boolean verified;
    @CreationTimestamp
    private Instant createdAt;
    private boolean deleted;
}