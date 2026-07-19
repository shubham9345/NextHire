package com.companyservice.CompanyService.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDateTime;

@Document(indexName = "jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDocument {

    @Id
    private String id;

    private String companyId;

    private String title;

    private String description;

    @Field(type = FieldType.Text)
    private String location;

    @Field(type = FieldType.Keyword)
    private String jobType;

    @Field(type = FieldType.Keyword)
    private String experienceLevel;
    private String companyName;
    private Integer salaryMin;

    private Integer salaryMax;

    private Integer maxCandidates;

    private Integer appliedCandidates;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date)
    private Instant stoppedAt;
}