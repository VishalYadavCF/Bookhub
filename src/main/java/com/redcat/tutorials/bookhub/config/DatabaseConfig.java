package com.redcat.tutorials.bookhub.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.dbname}")
    private String databaseName;

    @PostConstruct
    public void initializeDatabase() {
        String baseUrl = databaseUrl.substring(0, databaseUrl.lastIndexOf("/") + 1); // Extract base URL
        try (Connection connection = DriverManager.getConnection(baseUrl, username, password);
             Statement statement = connection.createStatement()) {

            // Check if the database exists, and create it if not
            String createDbQuery = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            statement.executeUpdate(createDbQuery);

        } catch (SQLException e) {
            throw new RuntimeException("Error while initializing the database: " + e.getMessage(), e);
        }
    }
}
