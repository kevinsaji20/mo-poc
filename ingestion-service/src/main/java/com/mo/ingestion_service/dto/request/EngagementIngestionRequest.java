package com.mo.ingestion_service.dto.request;


import com.mo.common.kafka.enums.DeviceType;
import com.mo.common.kafka.enums.IngestionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EngagementIngestionRequest (
        @NotNull
        UUID eventId,

        @NotNull
        UUID contentId,

        @NotNull
        UUID userId,

        @NotNull
        IngestionType eventType,

        @NotNull
        @PositiveOrZero
        Long playbackPositionMs,

        @NotNull
        @PositiveOrZero
        Long totalDurationMs,

        @NotNull
        OffsetDateTime eventTimeStamp,

        @NotNull
        UUID sessionId,

        DeviceType deviceType,

        @Pattern(
                regexp = "^[A-Z]{2}$",
                message = "Region must be ISO-3166 alpha-2 code"
        )
        String region,

        Long seekFromPosition,

        Long seekToPosition
) {
}
