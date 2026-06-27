package com.nextHire.AuthService.entity;

import com.nextHire.AuthService.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role roles;

    private boolean isVerified;

    private String CompanyName;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "provider")
    private String provider = "LOCAL";

    private LocalDateTime createdAt;
}