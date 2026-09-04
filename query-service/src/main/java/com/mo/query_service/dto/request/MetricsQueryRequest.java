package com.mo.query_service.dto.request;

import com.mo.query_service.enums.Granularity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.OffsetDateTime;

public record MetricsQueryRequest (
        OffsetDateTime from,
        OffsetDateTime to,
        Granularity granularity,

        @Min(0)
        Integer page,

        @Min(1)
        @Max(100)
        Integer size,

        String sortBy,
        String sortDir
) {
}
