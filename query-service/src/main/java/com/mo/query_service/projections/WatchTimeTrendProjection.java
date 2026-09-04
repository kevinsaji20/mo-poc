package com.mo.query_service.projections;

import java.time.OffsetDateTime;

public interface WatchTimeTrendProjection {
    OffsetDateTime getBucket();
    Long getTotalWatchTimeMs();
    Long getUniqueSessions();
    Long getUniqueUsers();
}
