package com.AIService.AIService.repository;


import com.AIService.AIService.entity.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswer, UUID> {

    Optional<InterviewAnswer> findByQuestionId(UUID questionId);

    // All answers for a session via question join
    @Query("SELECT a FROM InterviewAnswer a " +
            "JOIN a.question q WHERE q.session.id = :sessionId " +
            "ORDER BY q.orderIndex")
    List<InterviewAnswer> findAllBySessionId(@Param("sessionId") UUID sessionId);

    @Query("SELECT COUNT(a) FROM InterviewAnswer a " +
            "JOIN a.question q WHERE q.session.id = :sessionId")
    long countAnsweredBySessionId(@Param("sessionId") UUID sessionId);
}
