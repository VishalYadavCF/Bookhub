package com.redcat.tutorials.bookhub;
// Replace with your actual package

import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.io.File;
import java.nio.file.Files;

@SpringBootApplication
public class OpenApiGeneratorApplication {

    public static void main(String[] args) throws Exception {
        // Start a Spring app with minimal config
        ConfigurableApplicationContext context = new SpringApplicationBuilder(OpenApiGeneratorApplication.class)
                .web(WebApplicationType.SERVLET)
                .run("--server.port=8087"); // Use random port

        // Get OpenAPI bean
        OpenAPI openAPI = context.getBean(OpenAPI.class);

        // Convert to YAML
        String yaml = Yaml.pretty(openAPI);

        // Write to file in project root
        Files.write(new File("openapi.yaml").toPath(), yaml.getBytes());

        // Shutdown the application
        SpringApplication.exit(context, () -> 0);
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> openApi.getInfo()
                .title("BookHub API")
                .version("1.0")
                .description("The ultimate API for feline world domination through literature");
    }
}
