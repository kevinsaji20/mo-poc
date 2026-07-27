package com.mo.ingestion_service.kafka.producer;

import com.mo.common.kafka.constants.KafkaTopics;
import com.mo.common.kafka.enums.EventType;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.ingestion_service.dto.request.EngagementIngestionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IngestionEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishRawEngagementEvents(EngagementIngestionRequest ingestionEvent) {
        EventEnvelope<EngagementIngestionRequest> envelope = new EventEnvelope<>(
                UUID.randomUUID(),
                EventType.RAW_ENGAGEMENT_EVENT,
                OffsetDateTime.now(),
                ingestionEvent
        );

        kafkaTemplate.send(
                KafkaTopics.RAW_ENGAGEMENT_EVENTS,
                envelope.eventId().toString(),
                envelope
        );
    }
}
