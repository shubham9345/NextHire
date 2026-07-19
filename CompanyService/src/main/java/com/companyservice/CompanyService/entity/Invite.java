package com.companyservice.CompanyService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "invites",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_job_invite",
                        columnNames = {"user_id", "job_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID inviteId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID jobId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime invitedAt;
}