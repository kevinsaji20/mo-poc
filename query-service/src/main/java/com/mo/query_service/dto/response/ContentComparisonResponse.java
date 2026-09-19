package com.mo.query_service.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ContentComparisonResponse(
        UUID contentId,
        Long totalWatchTimeMs,
        Long avgWatchDurationMs,
        Long playCount,
        Long completeCount,
        BigDecimal completionRate,
        Integer peakConcurrentViewers,
        BigDecimal avgConcurrentViewers
) {
}
