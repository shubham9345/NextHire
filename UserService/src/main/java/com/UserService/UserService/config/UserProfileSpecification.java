package com.UserService.UserService.config;

import com.UserService.UserService.entity.UserProfile;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UserProfileSpecification {

    public static Specification<UserProfile> withFilters(
            Integer yearsOfExperience,
            String location,
            String headline,
            String skills
    ) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (yearsOfExperience != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("yearsOfExperience"),
                                yearsOfExperience
                        )
                );
            }

            if (StringUtils.hasText(location)) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("location")),
                                "%" + location.toLowerCase() + "%"
                        )
                );
            }

            if (StringUtils.hasText(headline)) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("headline")),
                                "%" + headline.toLowerCase() + "%"
                        )
                );
            }

            if (StringUtils.hasText(skills)) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("skills")),
                                "%" + skills.toLowerCase() + "%"
                        )
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
