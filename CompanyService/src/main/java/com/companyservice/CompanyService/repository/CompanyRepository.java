package com.companyservice.CompanyService.repository;

import com.companyservice.CompanyService.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository
        extends JpaRepository<Company, UUID> {

    Optional<Company> findByIdAndDeletedFalse(
            UUID companyId
    );
    Optional<Company> findByAuthUserIdAndDeletedFalse(UUID authUserId);
}
