package com.apigateway.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class gatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {

        return builder.routes()

                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("lb://AUTHSERVICE"))
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri("lb://USERSERVICE"))
                .route("ai-service", r -> r
                        .path("/api/ai/**")
                        .uri("lb://AI-SERVICE"))
                .route(
                        "ai-service",
                        r -> r.path("/api/companies/**")
                                .uri("lb://CompanyService")
                )

                .build();
    }
}