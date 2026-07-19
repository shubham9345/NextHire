package com.companyservice.CompanyService.controller;

import com.companyservice.CompanyService.dto.CompanyResponseDto;
import com.companyservice.CompanyService.dto.companyProfileRequest;
import com.companyservice.CompanyService.entity.Company;
import com.companyservice.CompanyService.service.CompanyService;
import com.companyservice.CompanyService.serviceImpl.CompanyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyServiceImpl;

    @PostMapping("/profile/{authUserId}")
    public CompanyResponseDto createProfile(

            @PathVariable UUID authUserId,

            @RequestBody companyProfileRequest request
    ) {

        return companyServiceImpl.createProfile(
                authUserId,
                request
        );
    }

    @PutMapping("/profile/{authUserId}")
    public ResponseEntity<CompanyResponseDto> updateProfile(
            @PathVariable UUID authUserId,
            @RequestBody companyProfileRequest request
    ) {
        CompanyResponseDto response = companyServiceImpl.updateProfile(authUserId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/profile/{companyId}")
    public ResponseEntity<String> deleteCompany(

            @PathVariable
            UUID companyId
    ) {

        companyServiceImpl.deleteCompany(
                companyId
        );

        return ResponseEntity.ok(
                "Company deleted successfully"
        );
    }

    @GetMapping("/profile/{authId}")
    public Company getProfile(

            @PathVariable
            UUID authId
    ) {

        return companyServiceImpl.getProfile(
                authId
        );
    }
    @GetMapping("/companyId/{authId}")
    public UUID getCompanyId(@PathVariable UUID authId) {
        return companyServiceImpl.getCompanyId(authId);
    }
}