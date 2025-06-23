package com.github.dimitryivaniuta.analytics.consumer;

import com.github.dimitryivaniuta.analytics.service.AnalyticsAggregatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * <h2>ClickConsumer</h2>
 * <p>Consumes <strong>click‑logged</strong> events from Kafka and hands them off
 * to {@link AnalyticsAggregatorService} for real‑time roll‑up aggregation.</p>
 *
 * <h3>Operational details</h3>
 * <ul>
 *   <li>❖ <strong>Manual acknowledgments</strong> – we commit the offset only
 *       after the aggregator finishes to guarantee at‑least‑once semantics.</li>
 *   <li>❖ <strong>Error handling</strong> – any exception logs the record key,
 *       value, and partition; the offset is <em>not</em> acknowledged so the
 *       message will be redelivered according to the container’s retry policy.</li>
 *   <li>❖ <strong>Concurrency</strong> – configured in
 *       <code>application.yml</code> (e.g. <code>concurrency: 3</code>) so the
 *       same class can be scaled horizontally without code changes.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClickConsumer {

    /** Application service that updates hourly/daily roll‑ups. */
    private final AnalyticsAggregatorService aggregator;
//
//    /**
//     * Kafka listener method; Spring creates one instance per assigned
//     * partition when concurrency &gt; 1.
//     *
//     * @param record consumed record containing a {@link ClickLoggedEvent}
//     * @param ack    manual acknowledgment – call {@link Acknowledgment#acknowledge()} when done
//     */
//    @KafkaListener(
//            topics = "click-logged",
//            groupId = "analytics-service",
//            containerFactory = "kafkaListenerContainerFactory")
//    public void consume(ConsumerRecord<String, ClickLoggedEvent> record,
//                        Acknowledgment ack) {
//        ClickLoggedEvent evt = record.value();
//        try {
//            aggregator.aggregate(evt);
//            ack.acknowledge();
//            if (log.isDebugEnabled()) {
//                log.debug("Aggregated click event for urlId={} partition={} offset={}",
//                        evt.getUrlId(), record.partition(), record.offset());
//            }
//        } catch (Exception ex) {
//            log.error("Failed to process click event urlId={} partition={} offset={} – will retry", \
//                    evt.getUrlId(), record.partition(), record.offset(), ex);
//            // no ack – rely on Kafka retry / DLT (dead‑letter topic) configuration
//        }
//    }
}
