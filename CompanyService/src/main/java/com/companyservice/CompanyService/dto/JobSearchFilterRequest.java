package com.companyservice.CompanyService.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobSearchFilterRequest {
    private String title;
    private String location;
    private LocalDateTime postedFrom;
    private LocalDateTime postedTo;
    private String jobType;
    private String experienceLevel;
    private int page = 0;
    private int size = 10;
}