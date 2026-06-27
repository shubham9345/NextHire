package com.org.InterviewExperience.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class InterviewRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interview_round_id;
    private String round_title;
    private String difficulty_level;
    private String duration;
    @OneToMany(mappedBy = "interview_round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodingQuestion> codingQuestionList;
    private String round_overview;
    @ManyToOne
    @JoinColumn(name = "interview_id")
    @JsonIgnore
    private Interview interview;

}
