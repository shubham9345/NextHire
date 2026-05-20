package com.companyservice.CompanyService.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CompanyResponseDto {

    private UUID id;

    private UUID authUserId;

    private String companyName;

    private String website;

    private String industry;

    private String description;

    private String location;

    private String logoUrl;

    private boolean verified;

}
