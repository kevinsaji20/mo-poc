package com.mo.query_service.projections;

import java.util.UUID;

public interface GenreTrendProjection {
    UUID getContentId();
    Long getTotalWatchTimeMs();
}
