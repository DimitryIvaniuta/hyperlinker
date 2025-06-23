package com.github.dimitryivaniuta.edge;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * <h2>EdgeApplication</h2>
 * <p>
 * Entry point for the Edge micro-service, responsible for resolving
 * shortened URLs, performing cache lookups, and forwarding redirect
 * responses. This application uses Spring WebFlux for reactive I/O
 * and connects to Redis for hot-path caching.
 * </p>
 *
 * <p>
 * To customize behavior, provide implementations of beans such as
 * {@code ReactiveRedisConnectionFactory} or override properties
 * under <code>spring.redis</code> and <code>spring.kafka</code> in
 * <code>application.yml</code>.
 * </p>
 */
@SpringBootApplication(scanBasePackages = "com.github.dimitryivaniuta.edge")
@EnableConfigurationProperties  // enable @ConfigurationProperties beans if any
public class EdgeApplication {

    /**
     * Main entry point. Boots the Spring application context
     * and starts the Netty server for reactive HTTP handling.
     *
     * @param args standard command-line arguments (ignored)
     */
    public static void main(String[] args) {
        SpringApplication.run(EdgeApplication.class, args);
    }
}