package com.mo.processing_service.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DropoffHeatmap(
        UUID contentId,
        OffsetDateTime windowStart,
        OffsetDateTime windowEnd,
        short positionBucket,
        int stopCount,
        OffsetDateTime computedAt
) {
}
