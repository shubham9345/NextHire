package com.org.InterviewExperience.repository;

import com.org.InterviewExperience.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    public List<Interview> findInterviewByUserId(Long UserId);
}
