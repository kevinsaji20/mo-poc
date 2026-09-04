package com.mo.query_service.dto.response;

import java.math.BigDecimal;

public record PlatformOverviewResponse (
        Long totalWatchTimeMs,
        Long avgWatchDurationMs,
        Long uniqueSessions,
        Long uniqueUsers,
        Long playCount,
        Long completeCount,
        BigDecimal completionRate,
        Integer peakConcurrentViewers,
        BigDecimal avgConcurrentViewers
) {
}
