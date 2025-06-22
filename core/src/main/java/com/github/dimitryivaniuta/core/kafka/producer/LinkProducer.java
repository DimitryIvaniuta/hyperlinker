package com.github.dimitryivaniuta.core.kafka.producer;

import com.github.dimitryivaniuta.core.event.LinkCreatedEvent;
import com.github.dimitryivaniuta.core.kafka.producer.KafkaProducerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * <h2>LinkProducer</h2>
 * <p>Produces a {@link LinkCreatedEvent} to the <code>link-created</code> topic
 * whenever a new short URL is persisted.  The event key is the <strong>alias</strong>
 * so that all messages for the same link land in the same partition, preserving order.</p>
 */
@Component
@RequiredArgsConstructor
public class LinkProducer {

    /** Spring‑managed template injected by {@link com.github.dimitryivaniuta.core.kafka.producer.KafkaProducerConfig}. */
    private final KafkaTemplate<String, LinkCreatedEvent> kafkaTemplate;

    /**
     * Send the event asynchronously.
     *
     * @param event fully populated event DTO
     */
    public void send(LinkCreatedEvent event) {
        String key = event.getAlias();
        kafkaTemplate.send(KafkaTopicNames.LINK_CREATED, key, event);
    }
}