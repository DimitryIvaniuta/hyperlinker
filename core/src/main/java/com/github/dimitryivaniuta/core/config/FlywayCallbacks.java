package com.github.dimitryivaniuta.core.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Event;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

/**
 * <p>Flyway callback that records every successful migration in an <code>migration_audit</code>
 * table and emits structured log messages.  Including this callback in the classpath
 * allows Spring‑Boot's auto‑configured Flyway bean to pick it up automatically.</p>
 *
 * <p>The callback is deliberately lightweight—executing only on the
 * {@link Event#AFTER_EACH_MIGRATE} event so it runs once per script and inside the
 * same transaction as the migration (where supported by the database).</p>
 *
 * <h2>Design goals</h2>
 * <ul>
 *   <li>   <b>Auditability</b> — Who/when/what migrated is stored in DB</li>
 *   <li>   <b>Idempotent</b> — The audit table is created if missing, no error if it already exists</li>
 *   <li>   <b>Fail‑fast</b> — Any SQL exception bubbles up and fails the whole migration</li>
 * </ul>
 *
 * <p>Flyway will discover this callback automatically as long as it is visible on the
 * application's classpath.  No further configuration is required.</p>
 */
@Slf4j
public class FlywayCallbacks implements Callback {

    /** Name of the table that stores audit entries. */
    private static final String AUDIT_TABLE = "migration_audit";

    /** SQL to lazily create the audit table if it does not already exist. */
    private static final String CREATE_AUDIT_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " + AUDIT_TABLE + " (" +
                    "    BIGINT PRIMARY KEY   DEFAULT nextval('CH_UNIQUE_ID'), " +
                    "    version      VARCHAR(50) NOT NULL, " +
                    "    description  VARCHAR(200), " +
                    "    installed_at TIMESTAMPTZ NOT NULL, " +
                    "    installed_by VARCHAR(100) NOT NULL" +
                    ")";

    /** SQL that inserts one audit row. */
    private static final String INSERT_AUDIT_SQL =
            "INSERT INTO " + AUDIT_TABLE +
                    " (version, description, installed_at, installed_by) VALUES (?,?,?,?)";

    @Override
    public boolean supports(Event event, Context context) {
        // We only care about AFTER_EACH_MIGRATE which fires after every individual script.
        return Event.AFTER_EACH_MIGRATE == event;
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        // Yes – we want the audit insert to commit/rollback with the migration.
        return true;
    }

    @Override
    public void handle(Event event, Context context) {
        Configuration config = context.getConfiguration();
        DataSource ds = config.getDataSource();

        try (Connection conn = ds.getConnection()) {
            // Ensure audit table exists (cheap if already created)
            try (PreparedStatement ddl = conn.prepareStatement(CREATE_AUDIT_TABLE_SQL)) {
                ddl.execute();
            }

            // Extract migration info from Flyway context
            String version      = context.getMigrationInfo().getVersion().getVersion();
            String description  = context.getMigrationInfo().getDescription();
            String installedBy  = config.getUser() != null ? config.getUser() : "unknown";

            // Insert audit record
            try (PreparedStatement dml = conn.prepareStatement(INSERT_AUDIT_SQL)) {
                dml.setString(1, version);
                dml.setString(2, description);
                dml.setObject(3, Instant.now());
                dml.setString(4, installedBy);
                dml.executeUpdate();
            }

            log.info("Recorded migration {} – {}", version, description);
        } catch (SQLException ex) {
            throw new FlywayException("Could not record migration audit", ex);
        }
    }

    /**
     * A human‑readable name that appears in Flyway diagnostics.
     * Returning a stable value is recommended for easier debugging.
     */
    @Override
    public String getCallbackName() {
        return "hyperlinker-core-flyway-callback";
    }
}
