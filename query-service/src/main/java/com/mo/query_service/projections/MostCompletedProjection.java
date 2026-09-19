package com.mo.query_service.projections;

import java.util.UUID;

public interface MostCompletedProjection {
    UUID getContentId();
    Long getCompleteCount();
}
