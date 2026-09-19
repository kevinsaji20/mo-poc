package com.mo.ingestion_service.kafka.producer;

import com.mo.common.kafka.constants.KafkaTopics;
import com.mo.common.kafka.enums.EventType;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.common.kafka.events.IngestionEvent;
import com.mo.ingestion_service.dto.request.EngagementIngestionRequest;
import com.mo.ingestion_service.mapper.IngestionEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IngestionEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final IngestionEventMapper ingestionEventMapper;

    public void publishRawEngagementEvents(EngagementIngestionRequest ingestionEvent) {
        IngestionEvent event = ingestionEventMapper.toEvent(ingestionEvent);
        EventEnvelope<IngestionEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID(),
                EventType.RAW_ENGAGEMENT_EVENTS,
                OffsetDateTime.now(),
                event
        );

        kafkaTemplate.send(
                KafkaTopics.RAW_ENGAGEMENT_EVENTS,
                envelope.eventId().toString(),
                envelope
        );
    }
}
