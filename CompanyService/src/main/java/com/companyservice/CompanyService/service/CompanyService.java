package com.companyservice.CompanyService.service;

import com.companyservice.CompanyService.dto.CompanyResponseDto;
import com.companyservice.CompanyService.dto.UpdateCompanyProfileRequest;
import com.companyservice.CompanyService.entity.Company;
import com.companyservice.CompanyService.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyResponseDto createProfile(

            UUID authUserId,

            UpdateCompanyProfileRequest request
    ) {

        Company company = new Company();

        company.setAuthUserId(authUserId);

        company.setCompanyName(
                request.getCompanyName()
        );

        company.setWebsite(
                request.getWebsite()
        );

        company.setIndustry(
                request.getIndustry()
        );

        company.setDescription(
                request.getDescription()
        );

        company.setLocation(
                request.getLocation()
        );

        company.setLogoUrl(
                request.getLogoUrl()
        );
        company.setCreatedAt(LocalDateTime.now());

        Company savedCompany =
                companyRepository.save(company);

        log.info(
                "Company profile created for {}",
                authUserId
        );

        return mapToDto(savedCompany);
    }


    private CompanyResponseDto mapToDto(
            Company company
    ) {

        return CompanyResponseDto.builder()
                .id(company.getId())
                .authUserId(
                        company.getAuthUserId()
                )
                .companyName(
                        company.getCompanyName()
                )
                .website(
                        company.getWebsite()
                )
                .industry(
                        company.getIndustry()
                )
                .description(
                        company.getDescription()
                )
                .location(
                        company.getLocation()
                )
                .logoUrl(
                        company.getLogoUrl()
                )
                .verified(
                        company.isVerified()
                )
                .build();
    }

    public void deleteCompany(
            UUID companyId
    ) {

        Company company =
                companyRepository
                        .findByIdAndDeletedFalse(
                                companyId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Company not found"
                                )
                        );

        company.setDeleted(true);

        companyRepository.save(company);

        log.info(
                "Company deleted successfully {}",
                companyId
        );
    }

    public CompanyResponseDto getProfile(
            UUID companyId
    ) {

        Company company =
                companyRepository
                        .findByIdAndDeletedFalse(
                                companyId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Company not found"
                                )
                        );

        return mapToDto(company);
    }
}