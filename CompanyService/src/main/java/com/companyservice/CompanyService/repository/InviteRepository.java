package com.companyservice.CompanyService.repository;


import com.companyservice.CompanyService.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InviteRepository extends JpaRepository<Invite, UUID> {

    boolean existsByUserIdAndJobId(UUID userId, UUID jobId);
    List<Invite> findByUserId(UUID userId);
}