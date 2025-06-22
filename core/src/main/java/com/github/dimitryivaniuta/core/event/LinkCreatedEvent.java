package com.github.dimitryivaniuta.core.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * <h2>LinkCreatedEvent</h2>
 * <p>Message emitted by the <strong>core</strong> micro‑service immediately after
 * a short URL has been successfully persisted.  The event is <em>fire‑and‑forget</em>;
 * consumers (e.g. analytics, email notification service) do not affect the
 * transaction that created the link.</p>
 *
 * <h3>Key‑design</h3>
 * <ul>
 *   <li>The Kafka record <strong>key</strong> is {@link #alias} so all events for
 *       a given link are stored in order within the same partition.</li>
 *   <li>Fields are final; the class is immutable and therefore thread‑safe.</li>
 *   <li>Implements {@link Serializable} so it can be cached or stored if needed.
 *       Jackson serialises it to JSON for Kafka transport.</li>
 * </ul>
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class LinkCreatedEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Primary key of the newly created URL row. */
    private final Long urlId;

    /** Public‑facing alias (slug) of the link; used as Kafka message key. */
    private final String alias;

    /** UTC timestamp when the link was created. */
    private final Instant createdAt;
}