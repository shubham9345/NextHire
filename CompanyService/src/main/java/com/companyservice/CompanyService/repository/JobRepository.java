package com.companyservice.CompanyService.repository;

import com.companyservice.CompanyService.entity.Job;
import com.companyservice.CompanyService.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JobRepository
        extends JpaRepository<Job, UUID> {

    Optional<Job> findByIdAndDeletedFalse(
            UUID jobId
    );

    @Query("SELECT j FROM Job j JOIN FETCH j.company WHERE j.company.id = :companyId AND j.deleted = false ORDER BY j.createdAt DESC")
    Page<Job> findByCompanyIdWithCompany(@Param("companyId") UUID companyId, Pageable pageable);

}
