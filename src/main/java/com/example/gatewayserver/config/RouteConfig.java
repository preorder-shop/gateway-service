package com.example.gatewayserver.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator ms1Route(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("user", r -> r.path("/api/v1/users/**")
                        .uri("http://localhost:8081"))
                .route("activity", r -> r.path("/api/v1/post/**")
                        .uri("http://localhost:8082"))
                .build();
    }
}