package com.mo.query_service.repository;

import com.mo.query_service.dto.response.PlatformOverviewResponse;
import com.mo.query_service.dto.response.ContentComparisonResponse;

import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AdminMetricsRepository {
    PlatformOverviewResponse getPlatformOverview(
            OffsetDateTime from,
            OffsetDateTime to
    );

    List<ContentComparisonResponse> contentComparison(
            List<UUID> contentIds,
            OffsetDateTime from,
            OffsetDateTime to
    );
}
