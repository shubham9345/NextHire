package com.companyservice.CompanyService.dto;

import com.companyservice.CompanyService.entity.JobType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobSearchFilterRequest {
    private String title;
    private String location;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime postedFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime postedTo;
    private String jobType;
    private String experienceLevel;
    private int page = 0;
    private int size = 10;
}