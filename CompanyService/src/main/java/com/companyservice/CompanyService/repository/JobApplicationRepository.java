package com.companyservice.CompanyService.repository;

import com.companyservice.CompanyService.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, UUID> {

    boolean existsByJob_IdAndCandidateProfileIdAndDeletedFalse(
            UUID jobId,
            UUID candidateProfileId
    );

    long countByJob_IdAndDeletedFalse(
            UUID jobId
    );

    List<JobApplication> findByJob_IdAndDeletedFalseOrderByAppliedAtDesc(
            UUID jobId
    );
}
