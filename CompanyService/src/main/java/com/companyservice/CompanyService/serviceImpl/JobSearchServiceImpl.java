package com.companyservice.CompanyService.serviceImpl;


import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.companyservice.CompanyService.dto.JobResponseDto;
import com.companyservice.CompanyService.dto.JobSearchFilterRequest;
import com.companyservice.CompanyService.entity.JobDocument;
import com.companyservice.CompanyService.entity.JobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSearchServiceImpl {

    private final ElasticsearchOperations elasticsearchOperations;

    public Page<JobResponseDto> searchJobs(JobSearchFilterRequest filter) {

        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        // only show ACTIVE jobs
        boolQuery.filter(f -> f
                .term(t -> t
                        .field("status")
                        .value("OPEN")
                )
        );

        // filter by title (partial/fuzzy match)
        if (StringUtils.hasText(filter.getTitle())) {
            boolQuery.must(m -> m
                    .match(t -> t
                            .field("title")
                            .query(filter.getTitle())
                            .fuzziness("AUTO")
                    )
            );
        }

        // filter by location (partial match)
        if (StringUtils.hasText(filter.getLocation())) {
            boolQuery.filter(f -> f
                    .matchPhrase(t -> t
                            .field("location")
                            .query(filter.getLocation())
                    )
            );
        }

        // filter by date range
        if (filter.getPostedFrom() != null || filter.getPostedTo() != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            boolQuery.filter(f -> f
                    .range(r -> r
                            .untyped(u -> {
                                u.field("createdAt");
                                if (filter.getPostedFrom() != null)
                                    u.gte(JsonData.of(filter.getPostedFrom().format(fmt)));
                                if (filter.getPostedTo() != null)
                                    u.lte(JsonData.of(filter.getPostedTo().format(fmt)));
                                return u;
                            })
                    )
            );
        }

        // filter by jobType (now a true keyword field)
        if (StringUtils.hasText(filter.getJobType())) {
            boolQuery.filter(f -> f
                    .term(t -> t
                            .field("jobType")
                            .value(filter.getJobType())
                    )
            );
        }

        // filter by experienceLevel (now a true keyword field)
        if (StringUtils.hasText(filter.getExperienceLevel())) {
            boolQuery.filter(f -> f
                    .term(t -> t
                            .field("experienceLevel")
                            .value(filter.getExperienceLevel())
                    )
            );
        }

        Query query = new Query.Builder()
                .bool(boolQuery.build())
                .build();

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable)
                .build();

        SearchHits<JobDocument> searchHits =
                elasticsearchOperations.search(searchQuery, JobDocument.class);

        List<JobResponseDto> jobs = searchHits.getSearchHits().stream()
                .map(hit -> mapToDto(hit.getContent()))
                .collect(Collectors.toList());

        return new PageImpl<>(jobs, pageable, searchHits.getTotalHits());
    }

    private JobResponseDto mapToDto(JobDocument doc) {
        return JobResponseDto.builder()
                .id(UUID.fromString(doc.getId()))
                .companyId(UUID.fromString(doc.getCompanyId()))
                .title(doc.getTitle())
                .companyName(doc.getCompanyName())
                .description(doc.getDescription())
                .location(doc.getLocation())
                .jobType(doc.getJobType())
                .experienceLevel(doc.getExperienceLevel())
                .salaryMin(doc.getSalaryMin())
                .salaryMax(doc.getSalaryMax())
                .maxCandidates(doc.getMaxCandidates())
                .appliedCandidates(doc.getAppliedCandidates())
                .status(JobStatus.valueOf(doc.getStatus()))
                .createdAt(doc.getCreatedAt())
                .stoppedAt(doc.getStoppedAt())
                .build();
    }
}
