package com.companyservice.CompanyService.repository;

import com.companyservice.CompanyService.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JobRepository
        extends JpaRepository<Job, UUID> {

    Optional<Job> findByIdAndDeletedFalse(
            UUID jobId
    );
}
