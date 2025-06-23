package com.github.dimitryivaniuta.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <h2>CoreApplication</h2>
 * <p>Main class for the Core microservice.  Manages
 * persistence, Flyway migrations, and publishes domain
 * events to Kafka.  Enables transaction management
 * for JPA operations.</p>
 *
 * <h3>Annotations</h3>
 * <ul>
 *   <li>{@link SpringBootApplication} &mdash; activates auto-config,
 *       component scanning for com.example.core and sub-packages.</li>
 *   <li>{@link EnableTransactionManagement} &mdash; allows database
 *       transactions via @Transactional annotations.</li>
 * </ul>
 */
@SpringBootApplication(scanBasePackages = "com.github.dimitryivaniuta.core")
@EnableTransactionManagement
public class CoreApplication {

    /**
     * Application entry point for the Core service.
     *
     * @param args standard Java command-line arguments
     * @return the Spring application context
     */
    @SuppressWarnings("resource")
    public static ConfigurableApplicationContext main(String[] args) {
        return SpringApplication.run(CoreApplication.class, args);
    }
}