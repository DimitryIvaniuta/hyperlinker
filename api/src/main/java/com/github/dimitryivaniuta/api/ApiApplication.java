package com.github.dimitryivaniuta.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <h2>ApiApplication</h2>
 * <p>Bootstrap class for the <strong>API</strong> micro‑service.  This layer
 * exposes public REST endpoints, handles authentication/authorization, and
 * delegates business logic to the <code>core</code> module over Feign or direct
 * service beans (depending on deployment topology).</p>
 *
 * <h3>Features enabled</h3>
 * <ul>
 *   <li><strong>@EnableScheduling</strong> &mdash; spins up Spring’s scheduled
 *       task executor (used for token clean‑up, rate‑limit bucket reset, etc.).</li>
 *   <li><strong>@EnableTransactionManagement</strong> &mdash; allows declarative
 *       <code>@Transactional</code> boundaries for composite controller flows.</li>
 *   <li><strong>@SpringBootApplication</strong> &mdash; meta‑annotation that
 *       turns on component scanning, auto‑config, and property support.</li>
 * </ul>
 */
@SpringBootApplication(scanBasePackages = "com.github.dimitryivaniuta.api")
@EnableScheduling
@EnableTransactionManagement
public class ApiApplication {

    /**
     * Application entry‑point.
     *
     * @param args standard command‑line arguments (none required)
     * @return never returns under normal circumstances; the JVM stays alive
     *         until Spring Context shutdown.
     */
    @SuppressWarnings("resource") // we deliberately don’t close the context here
    public static ConfigurableApplicationContext main(String[] args) {
        return SpringApplication.run(ApiApplication.class, args);
    }
}
