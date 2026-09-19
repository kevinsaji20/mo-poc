package com.mo.query_service.projections;

import java.util.UUID;

public interface TopContentProjection {
    UUID getContentId();
    Long getTotalWatchTimeMs();
}
