package com.companyservice.CompanyService.dto;

import lombok.Data;

@Data
public class companyProfileRequest {

    private String companyName;

    private String website;

    private String industry;

    private String description;

    private String location;

    private String logoUrl;
}