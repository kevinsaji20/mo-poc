package com.mo.query_service.dto.request;

import com.mo.query_service.enums.Granularity;

import java.time.OffsetDateTime;

public record MetricsQueryRequest (
        OffsetDateTime from,
        OffsetDateTime to,
        Granularity granularity,
        Integer page,
        Integer size,
        String sortBy,
        String sortDir
) {
}
