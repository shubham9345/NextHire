package com.companyservice.CompanyService.dto;


import com.companyservice.CompanyService.entity.JobStatus;
import com.companyservice.CompanyService.entity.JobType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class JobResponseDto {

    private UUID id;

    private UUID companyId;

    private String title;
    private String companyName;
    private String description;

    private String location;

    private String jobType;

    private String experienceLevel;

    private Integer salaryMin;

    private Integer salaryMax;

    private Integer maxCandidates;

    private Integer appliedCandidates;

    private JobStatus status;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private Instant stoppedAt;
}
