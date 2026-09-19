package com.mo.catalog_service.kafka.producer;

import com.mo.common.kafka.constants.KafkaTopics;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.common.kafka.events.ContentArchivedEvent;
import com.mo.common.kafka.events.ContentPublishedEvent;
import com.mo.common.kafka.enums.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ContentEventProducerTest {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private ContentEventProducer producer;

    @BeforeEach
    void setup() {
        kafkaTemplate = mock(KafkaTemplate.class);
        producer = new ContentEventProducer(kafkaTemplate);
    }

    @Test
    void publishContentPublished_sendsEnvelopeToTopic() {
        ContentPublishedEvent event = new ContentPublishedEvent(UUID.randomUUID(), "VIDEO", "title", OffsetDateTime.now());

        producer.publishContentPublished(event);

        ArgumentCaptor<EventEnvelope> captor = ArgumentCaptor.forClass(EventEnvelope.class);
        verify(kafkaTemplate).send(eq(KafkaTopics.CONTENT_PUBLISHED), anyString(), captor.capture());

        EventEnvelope envelope = captor.getValue();
        assertThat(envelope).isNotNull();
        assertThat(envelope.eventType()).isEqualTo(EventType.CONTENT_PUBLISHED);
        assertThat(envelope.payload()).isEqualTo(event);
    }

    @Test
    void publishContentArchived_sendsEnvelopeToTopic() {
        ContentArchivedEvent event = new ContentArchivedEvent(UUID.randomUUID(), OffsetDateTime.now());

        producer.publishContentArchived(event);

        ArgumentCaptor<EventEnvelope> captor = ArgumentCaptor.forClass(EventEnvelope.class);
        verify(kafkaTemplate).send(eq(KafkaTopics.CONTENT_ARCHIVED), anyString(), captor.capture());

        EventEnvelope envelope = captor.getValue();
        assertThat(envelope).isNotNull();
        assertThat(envelope.eventType()).isEqualTo(EventType.CONTENT_ARCHIVED);
        assertThat(envelope.payload()).isEqualTo(event);
    }
}
