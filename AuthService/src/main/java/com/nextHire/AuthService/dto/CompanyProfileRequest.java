package com.nextHire.AuthService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyProfileRequest {

    private String companyName;

    private String website;

    private String industry;

    private String description;

    private String location;

    private String logoUrl;
}