package com.github.dimitryivaniuta.core.kafka.producer;

import com.github.dimitryivaniuta.avro.LinkCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Collections;
import java.util.Map;

/**
 * <h2>KafkaProducerConfig</h2>
 * <p>
 * Spring {@code @Configuration} that sets up the Kafka producer factory and template
 * for sending {@link LinkCreatedEvent} messages from the Core service.
 * </p>
 * <h3>Key points</h3>
 * <ul>
 *   <li>Externalizes all settings under <code>spring.kafka.*</code> via {@link KafkaProperties}.</li>
 *   <li>Calls the non-deprecated {@code buildProducerProperties(SslBundles)} overload.</li>
 *   <li>Uses {@link StringSerializer} for keys and {@link JsonSerializer} for values.</li>
 * </ul>
 */
@Configuration
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    /**
     * Constructor injection of Kafka configuration.
     *
     * @param kafkaProperties populated from application.yml (spring.kafka.*)
     */
    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * Builds the {@link ProducerFactory} for messages keyed by {@code String}
     * and valued by {@link LinkCreatedEvent}.
     *
     * @return a fully configured ProducerFactory
     */
    @Bean
    public ProducerFactory<String, LinkCreatedEvent> linkProducerFactory() {
        // Pass null to avoid the deprecated no-arg overload
        Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
        // String key serializer
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // JSON value serializer
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        // Don’t include type headers in the payload
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Exposes a type-safe {@link KafkaTemplate} for sending
     * {@link LinkCreatedEvent} messages.
     *
     * @param producerFactory injected ProducerFactory bean
     * @return a ready-to-use KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, LinkCreatedEvent> linkKafkaTemplate(
            ProducerFactory<String, LinkCreatedEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
