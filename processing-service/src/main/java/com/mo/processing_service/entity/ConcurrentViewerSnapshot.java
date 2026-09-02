package com.mo.processing_service.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ConcurrentViewerSnapshot(
        UUID contentId,
        OffsetDateTime windowStart,
        OffsetDateTime windowEnd,
        int peakViewers,
        BigDecimal avgViewers,
        OffsetDateTime computedAt
) {
}
