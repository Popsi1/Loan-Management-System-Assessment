package com.example.usermoduleservice.config.swaggerConfig;//package com.example.usermodule.config.swaggerConfig;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

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
                        .addServersItem(new Server().url("http://localhost:8080").description("User Service Development Server"));
        }

        private Info getInfo() {
                return new Info()
                        .title("USER MANAGEMENT SYSTEM")
                        .description("A user management system (UMS) for managing user data and access in a fintech app.")
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