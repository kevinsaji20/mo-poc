package com.mo.catalog_service.kafka.producer;

import com.mo.common.kafka.constants.KafkaTopics;
import com.mo.common.kafka.enums.EventType;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.catalog_service.kafka.event.ContentArchivedEvent;
import com.mo.catalog_service.kafka.event.ContentPublishedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class ContentEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishContentPublished(ContentPublishedEvent event) {
        EventEnvelope<ContentPublishedEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID(),
                EventType.CONTENT_PUBLISHED,
                OffsetDateTime.now(),
                event
        );

        kafkaTemplate.send(
                KafkaTopics.CONTENT_PUBLISHED,
                envelope.eventId().toString(),
                envelope
        );
    }

    public void  publishContentArchived(ContentArchivedEvent event) {
        EventEnvelope<ContentArchivedEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID(),
                EventType.CONTENT_ARCHIVED,
                OffsetDateTime.now(),
                event
        );

        kafkaTemplate.send(
                KafkaTopics.CONTENT_ARCHIVED,
                envelope.eventId().toString(),
                envelope
        );
    }
}
