package com.github.dimitryivaniuta.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * <h2>DeletionRequest</h2>
 * <p>Represents a GDPR “right‑to‑be‑forgotten” request for a specific shortened
 * URL.  A row appears here when the owner (or a DPO) calls the delete endpoint
 * in the API.  The <strong>GdprPurger</strong> scheduled job later scans this
 * table and permanently removes both the URL metadata and all associated click
 * data once <code>scheduledPurgeAt</code> has passed.</p>
 *
 * <h3>Table characteristics</h3>
 * <ul>
 *   <li>Primary key <code>urlId</code> doubles as a foreign key to the
 *       <code>urls</code> table.</li>
 *   <li>No cascading — purger explicitly deletes dependent rows to keep the
 *       process auditable.</li>
 *   <li>Default purge window = 30 days, but callers may override by passing a
 *       custom timestamp.</li>
 * </ul>
 */
@Entity
@Table(name = "deletion_requests")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class DeletionRequest {

    /**
     * Matches the primary key in the <code>urls</code> table.  Stored as PK here
     * to guarantee one deletion record per link.
     */
    @Id
    @Column(name = "url_id")
    private Long urlId;

    /**
     * Timestamp when the deletion was requested (UTC).  Auto‑filled if not
     * supplied by the caller.
     */
    @Column(name = "requested_at", nullable = false, updatable = false)
    private Instant requestedAt;

    /**
     * When the data is due for permanent purge.  Defaults to
     * <code>requestedAt + 30 days</code> in {@link #prePersist()} if left null.
     */
    @Column(name = "scheduled_purge_at", nullable = false)
    private Instant scheduledPurgeAt;

    /**
     * Ensures sensible defaults before the entity is inserted.
     */
    @PrePersist
    private void prePersist() {
        if (requestedAt == null) {
            requestedAt = Instant.now();
        }
        if (scheduledPurgeAt == null) {
            scheduledPurgeAt = requestedAt.plus(30, ChronoUnit.DAYS);
        }
    }
}
