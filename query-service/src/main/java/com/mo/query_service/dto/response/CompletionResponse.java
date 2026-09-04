package com.mo.query_service.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CompletionResponse (
        OffsetDateTime timestamp,
        Long playCount,
        Long completeCount,
        BigDecimal completionRate
) {
}
