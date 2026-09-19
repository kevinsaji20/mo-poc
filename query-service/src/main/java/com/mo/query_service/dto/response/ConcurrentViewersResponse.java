package com.mo.query_service.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ConcurrentViewersResponse (
        OffsetDateTime bucket,
        Integer peakViewers,
        BigDecimal avgViewers
) {
}
