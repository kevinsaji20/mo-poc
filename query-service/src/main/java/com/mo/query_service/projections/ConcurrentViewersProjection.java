package com.mo.query_service.projections;

import java.math.BigDecimal;
import java.time.Instant;

public interface ConcurrentViewersProjection {
    Instant getBucket();
    Integer getPeakViewers();
    BigDecimal getAvgViewers();
}
