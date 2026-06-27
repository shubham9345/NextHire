package com.companyservice.CompanyService.service;

import com.companyservice.CompanyService.dto.CompanyResponseDto;
import com.companyservice.CompanyService.dto.companyProfileRequest;
import com.companyservice.CompanyService.entity.Company;

import java.util.UUID;

public interface CompanyService {

    CompanyResponseDto createProfile(
            UUID authUserId,
            companyProfileRequest request
    );

    void deleteCompany(
            UUID companyId
    );

    Company getProfile(
            UUID authId
    );

    CompanyResponseDto updateProfile(
            UUID authUserId,
            companyProfileRequest request
    );
}
