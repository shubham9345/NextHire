package com.AIService.AIService.entity;

import com.AIService.AIService.enums.InterviewEnums;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "interview_questions", indexes = {
        @Index(name = "idx_question_session", columnList = "session_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSession session;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private InterviewEnums.QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewEnums.Difficulty difficulty;

    // The key points the LLM expects in a good answer (stored as JSON string)
    @Column(name = "expected_key_points", columnDefinition = "TEXT")
    private String expectedKeyPoints;

    // 0-based order within the session
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private InterviewAnswer answer;
}
