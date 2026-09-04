package com.mo.query_service.projections;

import java.math.BigDecimal;
import java.util.UUID;

public interface ContentComparisonProjection {
    UUID getContentId();
    Long getTotalWatchTimeMs();
    Long getAvgWatchDurationMs();
    Long getPlayCount();
    Long getCompleteCount();
    Integer getPeekConcurrentViewers();
    BigDecimal getAvgConcurrentViewers();
}
