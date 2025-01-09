package com.example.apigatewayservice.config;

import com.example.apigatewayservice.filter.JwtAuthGatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/loan/**")
                        .filters(f -> f.filter(new JwtAuthGatewayFilter()))
                        .uri("lb://loan-service"))
                .route(p -> p.path("/user/**")
                        .filters(f -> f.filter(new JwtAuthGatewayFilter()))
                        .uri("lb://user-service"))
                .route(r -> r.path("/loan-service/v3/api-docs").and().method(HttpMethod.GET).uri("lb://loan-service"))
                .route(r -> r.path("/user-service/v3/api-docs").and().method(HttpMethod.GET).uri("lb://user-service"))
                .build();
    }

}
