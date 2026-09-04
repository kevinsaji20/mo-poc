package com.mo.query_service.dto.response;

import java.math.BigDecimal;

public record SummaryResponse(
        Long totalWatchTimeMs,
        Long avgWatchTimeMs,
        Long uniqueSessions,
        Long uniqueUsers,
        Long playCount,
        Long completeCount,
        BigDecimal completionRate,
        Integer peakViewers,
        BigDecimal avgViewers
) {
}
