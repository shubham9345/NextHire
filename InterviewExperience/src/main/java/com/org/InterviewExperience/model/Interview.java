package com.org.InterviewExperience.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interviewId;
    private String company_name;
    private String job_role;
    private String interview_result;
    private String salary_range;
    private String experience_level;
    private String difficulty_level;
    private LocalDate date_post;
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewRound> interviewRoundList;
    private String overall_exp;
    private String mode;
    private UUID userId;
}
