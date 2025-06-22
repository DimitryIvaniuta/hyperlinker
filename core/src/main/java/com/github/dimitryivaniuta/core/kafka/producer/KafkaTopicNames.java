package com.github.dimitryivaniuta.core.kafka.producer;

/**
 * Central place for topic names to remain consistent across producer and consumer
 * code bases.  In production, you might externalise these to Confluent Schema Registry
 * or a Topic Catalog service, but string constants are fine for now.
 */
public interface KafkaTopicNames {
    String LINK_CREATED  = "link-created";
    String CLICK_LOGGED  = "click-logged"; // produced by the edge micro‑service
}