package com.github.dimitryivaniuta.core.kafka.producer;

import com.github.dimitryivaniuta.core.event.LinkCreatedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
//import org.springframework.boot.autoconfigure.kafka.KafkaProperties.SslBundles;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.SslBundles;

import java.util.Map;

/**
 * <h2>KafkaProducerConfig</h2>
 * <p>Spring Boot {@code @Configuration} that wires a type‑safe
 * {@link KafkaTemplate} for {@link LinkCreatedEvent} messages.  Using a dedicated
 * template makes it impossible to accidentally send the wrong payload to the
 * topic.</p>
 */
@Configuration
@EnableConfigurationProperties(KafkaProperties.class) // ensures KafkaProperties bean exists
public class KafkaProducerConfig {

    /** Injected configuration properties mapped from <code>spring.kafka.*</code> in application.yml. */
    private final KafkaProperties kafkaProps;

    public KafkaProducerConfig(KafkaProperties kafkaProps) {
        this.kafkaProps = kafkaProps;
    }

    /**
     * Producer factory configured with JSON value serializer and String key serializer.
     */
    @Bean
    public ProducerFactory<String, LinkCreatedEvent> linkProducerFactory() {
//        Map<String, Object> props = kafkaProps.buildProducerProperties();
        Map<String, Object> props = kafkaProps.buildProducerProperties((SslBundles) null);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        // Ensure JSON excludes type headers for consumers using default deserializer
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Type‑safe template for LinkCreatedEvent.
     */
    @Bean
    public KafkaTemplate<String, LinkCreatedEvent> linkKafkaTemplate(
            ProducerFactory<String, LinkCreatedEvent> linkProducerFactory) {
        return new KafkaTemplate<>(linkProducerFactory);
    }

    /* ------------------------------------------------------------------ */
    /* Topic beans so Spring‑Kafka auto‑creates them on start‑up (dev only) */
    /* ------------------------------------------------------------------ */

    @Bean
    public NewTopic linkCreatedTopic() {
        // 3 partitions let us horizontally scale analytics consumers in dev.
        return new NewTopic(KafkaTopicNames.LINK_CREATED, 3, (short) 1);
    }
}