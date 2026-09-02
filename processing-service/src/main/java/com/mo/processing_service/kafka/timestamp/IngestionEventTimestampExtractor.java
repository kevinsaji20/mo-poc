package com.mo.processing_service.kafka.timestamp;

import com.mo.common.kafka.envelope.EventEnvelope;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class IngestionEventTimestampExtractor implements TimestampExtractor {
    @Override
    public long extract(
            ConsumerRecord<Object, Object> record,
            long partitionTime
    ) {

        EventEnvelope<?> envelope =
                (EventEnvelope<?>) record.value();

        if (envelope == null || envelope.occurredAt() == null) {
            return partitionTime;
        }

        return envelope.occurredAt()
                .toInstant()
                .toEpochMilli();
    }
}
