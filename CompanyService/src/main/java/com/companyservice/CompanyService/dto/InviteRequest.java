package com.companyservice.CompanyService.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class InviteRequest {
    private UUID userId;
    private UUID jobId;
}