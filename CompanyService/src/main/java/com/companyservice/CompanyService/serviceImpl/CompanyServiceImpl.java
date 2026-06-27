package com.companyservice.CompanyService.serviceImpl;

import com.companyservice.CompanyService.dto.CompanyResponseDto;
import com.companyservice.CompanyService.dto.companyProfileRequest;
import com.companyservice.CompanyService.entity.Company;
import com.companyservice.CompanyService.repository.CompanyRepository;
import com.companyservice.CompanyService.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyResponseDto createProfile(

            UUID authUserId,

            companyProfileRequest request
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
        company.setCreatedAt(Instant.now());

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

    public Company getProfile(
            UUID authId
    ) {

        return companyRepository
                .findByAuthUserIdAndDeletedFalse(
                        authId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Company not found"
                        )
                );
    }

    public CompanyResponseDto updateProfile(UUID authUserId, companyProfileRequest request) {
        Company company = companyRepository.findByAuthUserIdAndDeletedFalse(authUserId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (StringUtils.hasText(request.getCompanyName())) {
            company.setCompanyName(request.getCompanyName());
        }
        if (StringUtils.hasText(request.getWebsite())) {
            company.setWebsite(request.getWebsite());
        }
        if (StringUtils.hasText(request.getIndustry())) {
            company.setIndustry(request.getIndustry());
        }
        if (StringUtils.hasText(request.getDescription())) {
            company.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getLocation())) {
            company.setLocation(request.getLocation());
        }
        if (StringUtils.hasText(request.getLogoUrl())) {
            company.setLogoUrl(request.getLogoUrl());
        }

        Company updated = companyRepository.save(company);
        return mapToDto(updated);
    }

}