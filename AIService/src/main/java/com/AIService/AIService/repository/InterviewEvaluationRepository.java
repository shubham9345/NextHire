package com.AIService.AIService.repository;


import com.AIService.AIService.entity.InterviewEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterviewEvaluationRepository extends JpaRepository<InterviewEvaluation, UUID> {

    Optional<InterviewEvaluation> findByAnswerId(UUID answerId);

    @Query("SELECT e FROM InterviewEvaluation e " +
            "JOIN e.answer a JOIN a.question q " +
            "WHERE q.session.id = :sessionId ORDER BY q.orderIndex")
    List<InterviewEvaluation> findAllBySessionId(@Param("sessionId") UUID sessionId);

    @Query("SELECT AVG(e.score) FROM InterviewEvaluation e " +
            "JOIN e.answer a JOIN a.question q " +
            "WHERE q.session.id = :sessionId")
    Double averageScoreBySessionId(@Param("sessionId") UUID sessionId);
}
