package com.mo.query_service.projections;

import java.math.BigDecimal;

public interface MetricsSummaryProjection {
    Long getTotalWatchTimeMs();
    Long getAvgWatchDurationMs();
    Long getUniqueSessions();
    Long getUniqueUsers();
    Long getPlayCount();
    Long getCompleteCount();
    Integer getPeakViewers();
    BigDecimal getAvgViewers();
}
