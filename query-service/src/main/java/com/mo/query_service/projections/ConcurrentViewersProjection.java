package com.mo.query_service.projections;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface ConcurrentViewersProjection {
    OffsetDateTime getBucket();
    Integer getPeakViewers();
    BigDecimal getAvgViewers();
}
