package com.companyservice.CompanyService.repository;

import com.companyservice.CompanyService.entity.JobApplication;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, UUID> {

    boolean existsByJobIdAndCandidateAuthUserIdAndDeletedFalse(
            UUID jobId,
            UUID candidateAuthUserId
    );

    long countByJobIdAndDeletedFalse(
            UUID jobId
    );

    List<JobApplication> findByJobIdAndDeletedFalseOrderByAppliedAtDesc(
            UUID jobId
    );

    @Query("SELECT ja FROM JobApplication ja JOIN FETCH ja.job WHERE ja.candidateAuthUserId = :authUserId AND ja.deleted = false ORDER BY ja.appliedAt DESC")
    Page<JobApplication> findAppliedJobsWithJob(@Param("authUserId") UUID authUserId, Pageable pageable);

    List<JobApplication> findByJobIdAndDeletedFalseOrderByAtsScoreDesc(UUID jobId);

    JobApplication findByCandidateAuthUserId(UUID candidateAuthUserId);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE job_applications
            SET status = 'REJECTED'
            WHERE status = 'OPEN'
              AND applied_at < CURRENT_TIMESTAMP - INTERVAL '90 days'
            """, nativeQuery = true)
    int rejectExpiredApplications();

}
