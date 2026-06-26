package com.AIService.AIService.entity;


import com.AIService.AIService.enums.InterviewEnums;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "interview_evaluations", indexes = {
        @Index(name = "idx_eval_answer", columnList = "answer_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InterviewEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false, unique = true)
    private InterviewAnswer answer;

    // Score 0.0 – 10.0
    @Column(nullable = false)
    private Double score;

    // Human-readable LLM feedback for the candidate
    @Column(columnDefinition = "TEXT")
    private String feedback;

    // JSON breakdown: {"accuracy": 8, "depth": 7, "clarity": 9}
    @Column(name = "rubric_json", columnDefinition = "TEXT")
    private String rubricJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InterviewEnums.EvaluationStatus status = InterviewEnums.EvaluationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "evaluated_at", updatable = false)
    private LocalDateTime evaluatedAt;
}