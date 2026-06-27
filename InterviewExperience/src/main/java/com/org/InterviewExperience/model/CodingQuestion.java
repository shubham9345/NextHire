package com.org.InterviewExperience.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CodingQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qId;
    private String questionTitle;
    private String description;
    private String difficultyLevel;
    @ManyToOne
    @JoinColumn(name = "round_id")
    @JsonIgnore
    private InterviewRound interview_round;
}
