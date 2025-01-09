package com.example.apigatewayservice.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthGatewayFilter implements GatewayFilter {

    private static String secret;

    private Logger logger = LoggerFactory.getLogger(JwtAuthGatewayFilter.class);

    @Value("${jwt.secret}")
    private String injectedSecret;

    // Set the static field after Spring injection
    @PostConstruct
    public void init() {
        secret = injectedSecret;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Check for Authorization header and Bearer token
        if (authHeader != null && !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header is missing or malformed.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                // Extract and parse JWT token
                String token = authHeader.substring(7);

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secret.getBytes())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // Add the "X-User-Role" header with the role from JWT claims
                exchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-User-Role", claims.get("role", String.class))
                                .header("X-User-Id", claims.get("user-id", String.class))
                                .build())
                        .build();
            } catch (Exception e) {
                logger.error("Invalid JWT token: {}", e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
        return chain.filter(exchange);

    }
}
