package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = isLocalOrigin(frontendUrl)
                ? new String[]{"http://localhost:5173", "http://127.0.0.1:5173"}
                : new String[]{frontendUrl};

        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(false)
                .maxAge(3600);
    }

    private boolean isLocalOrigin(String origin) {
        return "http://localhost:5173".equals(origin)
                || "http://127.0.0.1:5173".equals(origin);
    }
}
