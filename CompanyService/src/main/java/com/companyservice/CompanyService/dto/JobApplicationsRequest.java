package com.companyservice.CompanyService.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobApplicationsRequest {

    @NotNull
    @Min(0)
    private Integer appliedCandidates;
}
