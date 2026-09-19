package com.mo.processing_service.kafka.timestamp;

import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.common.kafka.enums.EventType;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IngestionEventTimestampExtractorTest {

    @Test
    void extract_usesEnvelopeOccurredAtWhenPresent() {
        OffsetDateTime occurredAt = OffsetDateTime.parse("2024-01-02T03:04:05Z");
        EventEnvelope<Object> envelope = new EventEnvelope<>(UUID.randomUUID(), EventType.RAW_ENGAGEMENT_EVENTS, occurredAt, new Object());

        ConsumerRecord<Object, Object> record = new ConsumerRecord<>(
                "topic",
                0,
                0L,
                0L,
                org.apache.kafka.common.record.TimestampType.NO_TIMESTAMP_TYPE,
                0L,
                0,
                0,
                null,
                envelope
        );

        long timestamp = new IngestionEventTimestampExtractor().extract(record, 999L);

        assertThat(timestamp).isEqualTo(occurredAt.toInstant().toEpochMilli());
    }

    @Test
    void extract_returnsPartitionTimeWhenEnvelopeMissingOccurredAt() {
        EventEnvelope<Object> envelope = new EventEnvelope<>(UUID.randomUUID(), EventType.RAW_ENGAGEMENT_EVENTS, null, new Object());

        ConsumerRecord<Object, Object> record = new ConsumerRecord<>(
                "topic",
                0,
                0L,
                0L,
                org.apache.kafka.common.record.TimestampType.NO_TIMESTAMP_TYPE,
                0L,
                0,
                0,
                null,
                envelope
        );

        long timestamp = new IngestionEventTimestampExtractor().extract(record, 123L);

        assertThat(timestamp).isEqualTo(123L);
    }
}
