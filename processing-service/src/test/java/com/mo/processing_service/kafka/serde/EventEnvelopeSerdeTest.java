package com.mo.processing_service.kafka.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.common.kafka.enums.EventType;
import com.mo.common.kafka.envelope.EventEnvelope;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EventEnvelopeSerdeTest {

    @Test
    void serde_roundTripsEventEnvelope() {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        EventEnvelopeSerde<String> serde = new EventEnvelopeSerde<>(mapper, String.class);
        EventEnvelope<String> envelope = new EventEnvelope<>(
                UUID.randomUUID(),
                EventType.RAW_ENGAGEMENT_EVENTS,
                OffsetDateTime.now(),
                "hello"
        );

        byte[] bytes = serde.serializer().serialize("topic", envelope);
        EventEnvelope<String> deserialized = serde.deserializer().deserialize("topic", bytes);

        assertThat(deserialized.eventId()).isEqualTo(envelope.eventId());
        assertThat(deserialized.eventType()).isEqualTo(envelope.eventType());
        assertThat(deserialized.payload()).isEqualTo(envelope.payload());
        assertThat(deserialized.occurredAt().toInstant()).isEqualTo(envelope.occurredAt().toInstant());
    }
}
