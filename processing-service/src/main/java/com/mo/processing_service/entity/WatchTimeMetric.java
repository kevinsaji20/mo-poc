package com.mo.processing_service.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WatchTimeMetric (
        UUID contentId,
        OffsetDateTime windowStart,
        OffsetDateTime windowEnd,
        long totalWatchTimeMs,
        long avgWatchDurationMs,
        int uniqueSessions,
        int uniqueUsers,
        OffsetDateTime computedAt
){
}
