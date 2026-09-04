package com.mo.query_service.projections;

import java.time.OffsetDateTime;

public interface CompletionProjection {
    OffsetDateTime getBucket();
    Long getPlayCount();
    Long getCompleteCount();
}
