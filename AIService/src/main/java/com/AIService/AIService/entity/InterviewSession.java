package com.AIService.AIService.entity;


import com.AIService.AIService.enums.InterviewEnums;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "interview_sessions", indexes = {
        @Index(name = "idx_session_user",   columnList = "user_id"),
        @Index(name = "idx_session_job",    columnList = "job_id"),
        @Index(name = "idx_session_status", columnList = "status")
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    private String email;

    // Snapshot of job title at session creation (denormalised for report generation)
    @Column(name = "job_title")
    private String jobTitle;

    // Snapshot of job description used to generate questions
    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    // Required skills extracted from JD (comma-separated or JSON array as string)
    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InterviewEnums.SessionStatus status = InterviewEnums.SessionStatus.PENDING;

    @Column(name = "overall_score")
    private Double overallScore;

    @Column(name = "report_url")
    private String reportUrl;  // Firebase URL to generated PDF

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<InterviewQuestion> questions = new ArrayList<>();
}
