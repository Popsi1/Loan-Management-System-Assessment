package com.example.usermodule.config.swaggerConfig;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

        private static final String SCHEME_NAME_BEARER = "bearerAuth";
        private static final String SCHEME_BEARER = "bearer";
        private static final String SCHEME_BEARER_FORMAT = "JWT";

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .addServersItem(new Server().url("/"))
                        .info(getInfo())
                        .components(new Components()
                                .addSecuritySchemes(SCHEME_NAME_BEARER, createSecurityScheme()))
                        .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME_BEARER));
        }

        private Info getInfo() {
                return new Info()
                        .title("USER MANAGEMENT SYSTEM")
                        .description("""
                        A user management system (LMS) in a fintech app streamlines the end-to-end process of managing loans,
                        from application to repayment.
                        """)
                        .version("0.0.1");
        }

        private SecurityScheme createSecurityScheme() {
                return new SecurityScheme()
                        .name(SCHEME_NAME_BEARER)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(SCHEME_BEARER)
                        .bearerFormat(SCHEME_BEARER_FORMAT);
        }

}