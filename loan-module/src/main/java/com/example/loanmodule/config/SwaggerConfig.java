package com.example.loanmodule.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("LOAN MANAGEMENT SYSTEM")
                        .description("""
                        A loan management system (LMS) in a fintech app streamlines the end-to-end process of managing loans,
                        from application to repayment.
                        """)
                        .version("1.0"));
    }
}
