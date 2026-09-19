package com.mo.query_service.projections;

import java.time.Instant;

public interface CompletionProjection {
    Instant getBucket();
    Long getPlayCount();
    Long getCompleteCount();
}
