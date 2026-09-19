package com.mo.common.kafka.envelope;

import com.mo.common.kafka.enums.EventType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EventEnvelope<T>(
        UUID eventId,
        EventType eventType,
        OffsetDateTime occurredAt,
        T payload
) {
}
