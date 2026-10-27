package com.tms.transportationManagementSystem.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI tmsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transportation Management System API")
                        .version("v1")
                        .description("""
                                Backend APIs for the TMS frontend.

                                Authentication: call the login endpoint under /auth, \
                                then click "Authorize" and paste the JWT \
                                (without the "Bearer " prefix).
                                """))
                // Apply JWT auth to all endpoints by default
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
