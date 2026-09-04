package com.mo.query_service.projections;

import java.math.BigDecimal;

public interface PlatformOverviewProjection {
    Long getTotalWatchTimeMs();
    Long getAvgWatchDurationMs();
    Long getUniqueSessions();
    Long getUniqueUsers();
    Long getPlayCount();
    Long getCompleteCount();
    Integer getPeakConcurrentViewers();
    BigDecimal getAvgConcurrentViewers();
}
