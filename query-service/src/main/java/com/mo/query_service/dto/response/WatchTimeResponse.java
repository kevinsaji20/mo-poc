package com.mo.query_service.dto.response;

import java.time.OffsetDateTime;

public record WatchTimeResponse(
        OffsetDateTime timestamp,
        Long totalWatchTimeMs,
        Long uniqueSessions,
        Long uniqueUsers
) {
}
