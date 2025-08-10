package com.banking.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Banking Application API Gateway")
                        .description("API Gateway for Banking Application Microservices\n\n" +
                            "## Overview\n" +
                            "This API Gateway provides a unified entry point for all banking microservices including:\n" +
                            "- User Management\n" +
                            "- Account Management\n" +
                            "- Transaction Processing\n" +
                            "- Payment Processing\n" +
                            "- Notification Services\n\n" +
                            "## Authentication\n" +
                            "All protected endpoints require a valid JWT token in the Authorization header.\n" +
                            "Format: `Bearer <token>`\n\n" +
                            "## Rate Limiting\n" +
                            "API calls are rate-limited per user and IP address to ensure fair usage.\n\n" +
                            "## Circuit Breaker\n" +
                            "The gateway implements circuit breakers for all microservices to ensure resilience.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Banking Team")
                                .email("support@bankingapp.com")
                                .url("https://bankingapp.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.bankingapp.com")
                                .description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication")))
                .tags(Arrays.asList(
                        new Tag().name("Authentication").description("User authentication and authorization endpoints"),
                        new Tag().name("Users").description("User management operations"),
                        new Tag().name("Accounts").description("Bank account operations"),
                        new Tag().name("Transactions").description("Financial transaction operations"),
                        new Tag().name("Payments").description("Payment processing operations"),
                        new Tag().name("Notifications").description("Notification service operations"),
                        new Tag().name("Health").description("System health and monitoring endpoints"),
                        new Tag().name("Admin").description("Administrative operations (Admin only)")
                ));
    }
}
