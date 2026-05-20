package com.apigateway.api_gateway.utils;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(

            "/api/auth/login",
            "/api/auth/signup",
            "/api/auth/company/signup"
    );

    public Predicate<String> isSecured =
            uri -> openApiEndpoints
                    .stream()
                    .noneMatch(uri::contains);
}
