package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Validates that required database environment variables are set before
 * Spring Boot attempts to initialize the datasource.
 *
 * Without this check, a missing DB_URL causes Hibernate to fail with the
 * confusing error:
 *   "Unable to determine Dialect without JDBC metadata"
 *
 * This validator fails fast with a clear message if any required variable
 * is missing or empty. It never exposes secret values.
 */
@Configuration
public class DatabaseConfigValidator {

    @Value("${DB_URL:}")
    private String dbUrl;

    @Value("${DB_USERNAME:}")
    private String dbUsername;

    @Value("${DB_PASSWORD:}")
    private String dbPassword;

    @PostConstruct
    public void validateDatabaseConfiguration() {
        if (dbUrl == null || dbUrl.isBlank()) {
            throw new IllegalStateException(
                "Missing required environment variable DB_URL. "
                + "Set DB_URL in the Render dashboard environment variables for the backend service."
            );
        }
        if (dbUsername == null || dbUsername.isBlank()) {
            throw new IllegalStateException(
                "Missing required environment variable DB_USERNAME. "
                + "Set DB_USERNAME in the Render dashboard environment variables for the backend service."
            );
        }
        if (dbPassword == null || dbPassword.isBlank()) {
            throw new IllegalStateException(
                "Missing required environment variable DB_PASSWORD. "
                + "Set DB_PASSWORD in the Render dashboard environment variables for the backend service."
            );
        }

        // Basic JDBC URL sanity check (does not expose credentials)
        if (!dbUrl.startsWith("jdbc:postgresql://")) {
            throw new IllegalStateException(
                "DB_URL must be a valid PostgreSQL JDBC URL starting with jdbc:postgresql://. "
                + "Current value does not match expected format."
            );
        }
    }
}
