package com.mo.query_service.projections;

import java.time.Instant;

public interface WatchTimeProjection {
    Instant getBucket();
    Long getTotalWatchTimeMs();
    Long getUniqueSessions();
    Long getUniqueUsers();
}
