package com.github.dimitryivaniuta.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * <h2>ClickRaw</h2>
 * <p>
 *  JPA entity representing a single redirect event ("click")
 *  captured by the <strong>edge</strong> micro‑service.  This table is
 *  the write‑heavy append‑only log that later feeds roll‑up analytics.
 * </p>
 * <p>
 *  In PostgreSQL the physical table is partitioned monthly by the
 *  <code>occurredAt</code> column (see Flyway migration V2) but JPA
 *  interacts with it as a single logical table.
 * </p>
 *
 * <h3>Design notes</h3>
 * <ul>
 *   <li>  <strong>Privacy‑by‑design:</strong> IP addresses are salted &amp; SHA‑256 hashed
 *       before storage&nbsp;→ the raw PII never touches the database.</li>
 *   <li>  <strong>Write throughput:</strong> no foreign‑key cascade or ON UPDATE features; a plain
 *       FK to <code>urls</code> keeps referential integrity while allowing partition pruning.</li>
 *   <li>  <strong>Time‑series:</strong> <code>occurredAt</code> is stored in UTC (TIMESTAMPTZ) for
 *       accurate cross‑region analytics.</li>
 * </ul>
 */
@Entity
@Table(name = "clicks_raw")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ClickRaw {

    /**
     * Surrogate primary key (auto‑incremented by PostgreSQL sequence).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Foreign‑key reference to the <code>urls</code> table (mandatory).
     */
    @Column(name = "url_id", nullable = false, updatable = false)
    private Long urlId;

    /**
     * Timestamp in UTC when the redirect occurred.
     * <p>Automatically initialised in {@link #prePersist()} if not supplied.</p>
     */
    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    /**
     * SHA‑256 hexadecimal hash of the visitor's IP address (salted client‑side).
     */
    @Column(name = "ip_hash", length = 64, nullable = false, updatable = false)
    private String ipHash;

    /**
     * Raw User‑Agent string (may be <code>null</code> if header absent).
     */
    @Column(name = "ua", columnDefinition = "text")
    private String userAgent;

    /**
     * HTTP referrer header trimmed to 2kB max (can be <code>null</code>).
     */
    @Column(name = "referrer", columnDefinition = "text")
    private String referrer;

    /**
     * Two‑letter ISO‑3166 country code resolved from Geo‑IP, or <code>null</code>.
     */
    @Column(name = "country", length = 2)
    private String country;

    /**
     * Parsed device family (e.g. "mobile", "tablet", "desktop").
     */
    @Column(name = "device", length = 32)
    private String device;

    /**
     * Automatically sets <code>occurredAt</code> just before insert if blank.
     */
    @PrePersist
    private void prePersist() {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
