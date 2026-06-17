package com.companyservice.CompanyService.repository;

import com.companyservice.CompanyService.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, UUID> {

    boolean existsByJobIdAndCandidateProfileIdAndDeletedFalse(
            UUID jobId,
            UUID candidateProfileId
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
}
