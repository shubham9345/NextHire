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

    @PutMapping("/profile")
    public CompanyResponseDto updateProfile(

            @RequestHeader("X-User-Id")
            UUID authUserId,

            @RequestBody
            UpdateCompanyProfileRequest request
    ) {

        return companyService.updateProfile(
                authUserId,
                request
        );
    }
    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteCompany(

            @RequestHeader("X-User-Id")
            UUID authUserId
    ) {

        companyService.deleteCompany(
                authUserId
        );

        return ResponseEntity.ok(
                "Company deleted successfully"
        );
    }

    @GetMapping("/profile")
    public CompanyResponseDto getProfile(

            @RequestHeader("X-User-Id")
            UUID authUserId
    ) {

        return companyService.getProfile(
                authUserId
        );
    }
}