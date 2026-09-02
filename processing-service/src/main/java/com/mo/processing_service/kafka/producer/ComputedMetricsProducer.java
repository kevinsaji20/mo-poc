package com.mo.processing_service.kafka.producer;

import com.mo.common.kafka.constants.KafkaTopics;
import com.mo.common.kafka.enums.EventType;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.common.kafka.events.ComputedMetricEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ComputedMetricsProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishComputedMetricsEvents(ComputedMetricEvent event) {
        EventEnvelope<ComputedMetricEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID(),
                EventType.COMPUTED_METRICS_EVENTS,
                OffsetDateTime.now(),
                event
        );

        kafkaTemplate.send(
                KafkaTopics.COMPUTED_METRICS_EVENTS,
                envelope.eventId().toString(),
                envelope
        );
    }
}
