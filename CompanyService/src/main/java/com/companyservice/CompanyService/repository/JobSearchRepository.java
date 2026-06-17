package com.companyservice.CompanyService.repository;

import com.companyservice.CompanyService.entity.JobDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface JobSearchRepository extends ElasticsearchRepository<JobDocument, String> {
}