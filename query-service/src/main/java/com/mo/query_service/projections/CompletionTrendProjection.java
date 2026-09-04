package com.mo.query_service.projections;

import java.time.OffsetDateTime;

public interface CompletionTrendProjection {
    OffsetDateTime getBucket();
    Long getPlayCount();
    Long getCompleteCount();
}
