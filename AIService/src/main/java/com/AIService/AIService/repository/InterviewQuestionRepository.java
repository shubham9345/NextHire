package com.AIService.AIService.repository;


import com.AIService.AIService.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, UUID> {

    List<InterviewQuestion> findBySessionIdOrderByOrderIndex(UUID sessionId);

    long countBySessionId(UUID sessionId);
}