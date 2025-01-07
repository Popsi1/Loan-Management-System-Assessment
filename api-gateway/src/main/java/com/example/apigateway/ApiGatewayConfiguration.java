package com.example.apigateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/api/loans/**")
                        .filters(f -> f.filter(new JwtAuthGatewayFilter()))
                        .uri("lb://loan-module"))
                .route(p -> p.path("/api/users/**")
                        .filters(f -> f.filter(new JwtAuthGatewayFilter()))
                        .uri("lb://loanUser-module"))
                .build();
    }
}
