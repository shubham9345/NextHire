package com.companyservice.CompanyService.controller;

import com.companyservice.CompanyService.dto.CompanyResponseDto;
import com.companyservice.CompanyService.dto.UpdateCompanyProfileRequest;
import com.companyservice.CompanyService.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/profile/{authUserId}")
    public CompanyResponseDto updateProfile(

            @PathVariable UUID authUserId,

            @RequestBody UpdateCompanyProfileRequest request
    ) {

        return companyService.createProfile(
                authUserId,
                request
        );
    }

    @DeleteMapping("/profile/{companyId}")
    public ResponseEntity<String> deleteCompany(

            @PathVariable
            UUID companyId
    ) {

        companyService.deleteCompany(
                companyId
        );

        return ResponseEntity.ok(
                "Company deleted successfully"
        );
    }

    @GetMapping("/profile/{companyId}")
    public CompanyResponseDto getProfile(

            @PathVariable
            UUID companyId
    ) {

        return companyService.getProfile(
                companyId
        );
    }
}