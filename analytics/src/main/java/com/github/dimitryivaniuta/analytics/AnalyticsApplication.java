package com.github.dimitryivaniuta.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <h2>AnalyticsApplication</h2>
 * Main entry point for the Analytics microservice.
 */
@SpringBootApplication
public class AnalyticsApplication {

    /**
     * Bootstraps the Spring Boot application.
     * @param args standard main args
     */
    public static void main(String[] args) {
        SpringApplication.run(AnalyticsApplication.class, args);
    }
}