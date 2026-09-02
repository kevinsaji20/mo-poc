package com.mo.processing_service.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CompletionMetric(
        UUID contentId,
        OffsetDateTime windowStart,
        OffsetDateTime windowEnd,
        int playCount,
        int completeCount,
        BigDecimal completionRate,
        OffsetDateTime computedAt
) {
}
