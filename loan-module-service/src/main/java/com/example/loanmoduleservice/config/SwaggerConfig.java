package com.example.loanmoduleservice.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class SwaggerConfig {

    private static final String SCHEME_NAME_BEARER = "bearerAuth";
    private static final String SCHEME_BEARER = "bearer";
    private static final String SCHEME_BEARER_FORMAT = "JWT";


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(getInfo())
                .components(new Components()
                        .addSecuritySchemes(SCHEME_NAME_BEARER, createSecurityScheme()))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME_BEARER))
                        .addServersItem(new Server().url("http://localhost:8080").description("loan Service Development Server"));
    }

    private Info getInfo() {
        return new Info()
                .title("LOAN MANAGEMENT SYSTEM")
                .description("A loan management system (UMS) for managing user to apply and repay loans.")
                .version("1.0.0");
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name(SCHEME_NAME_BEARER)
                .type(SecurityScheme.Type.HTTP)
                .scheme(SCHEME_BEARER)
                .bearerFormat(SCHEME_BEARER_FORMAT);
    }
}

