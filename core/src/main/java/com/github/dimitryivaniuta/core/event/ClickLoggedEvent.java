package com.github.dimitryivaniuta.core.event;

import lombok.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * <h2>ClickLoggedEvent</h2>
 * <p>Produced by the <strong>edge</strong> micro‑service each time it processes a
 * redirect.  The message is consumed by the analytics service to update
 * near-real-time roll-ups.</p>
 *
 * <h3>Payload rationale</h3>
 * <ul>
 *   <li><strong>urlId</strong> - joins to the <code>urls</code> table for
 *       enrichment.</li>
 *   <li><strong>occurredAt</strong> - event time in UTC for correct
 *       windowing (Kafka timestamp may be ingestion-time).</li>
 *   <li><strong>country</strong>, <strong>device</strong> are denormalized to
 *       avoid a second geo/User-Agent parse in the consumer.</li>
 * </ul>
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ClickLoggedEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Foreign key back to the <code>urls</code> table. */
    private final Long urlId;

    /** Event timestamp in UTC. */
    private final Instant occurredAt;

    /** Two‑letter ISO country code resolved at edge. */
    private final String country;

    /** Device family (mobile / tablet / desktop). */
    private final String device;
}