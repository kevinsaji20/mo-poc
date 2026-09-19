package com.mo.common.kafka.events;

import com.mo.common.kafka.enums.DeviceType;
import com.mo.common.kafka.enums.IngestionType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IngestionEvent (
        UUID eventId,
        UUID contentId,
        UUID userId,
        IngestionType eventType,
        Long playbackPositionMs,
        Long totalDurationMs,
        OffsetDateTime eventTimeStamp,
        UUID sessionId,
        DeviceType deviceType,
        String region,
        Long seekFromPosition,
        Long seekToPosition
) {
}
