package com.redcat.tutorials.bookhub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // OpenAPI configuration can be added here if needed
    // For example, you can customize the OpenAPI documentation properties
    // using the @OpenAPIDefinition annotation or other OpenAPI-related annotations.

    @Bean
    public OpenAPI customOpenAPI() {
        // Add any OpenAPI configuration here if needed
        return new OpenAPI()
                .info(new Info().title("Book Hub API").title("Book Hub API").version("1.0").description("API for the evil cat's book domination system"));
    }
}
