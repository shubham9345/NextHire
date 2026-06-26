package com.AIService.AIService.repository;


import com.AIService.AIService.entity.InterviewSession;
import com.AIService.AIService.enums.InterviewEnums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {

    List<InterviewSession> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<InterviewSession> findByUserIdAndStatus(UUID userId, InterviewEnums.SessionStatus status);

    // Count active sessions for rate-limiting per user
    @Query("SELECT COUNT(s) FROM InterviewSession s WHERE s.userId = :userId " +
            "AND s.status IN ('PENDING','QUESTIONS_READY','IN_PROGRESS','EVALUATING')")
    long countActiveSessionsByUser(@Param("userId") UUID userId);

    List<InterviewSession> findByJobIdAndStatus(UUID jobId, InterviewEnums.SessionStatus status);
}