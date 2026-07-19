package com.org.InterviewExperience.repository;

import com.org.InterviewExperience.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    public List<Interview> findInterviewByUserId(UUID UserId);
}
