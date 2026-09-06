package com.mo.query_service.repository;

import com.mo.query_service.dto.response.ContentComparisonResponse;
import com.mo.query_service.dto.response.PlatformOverviewResponse;
import com.mo.query_service.repository.query.AdminMetricsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AdminMetricsRepositoryImpl implements AdminMetricsRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public PlatformOverviewResponse getPlatformOverview(
            OffsetDateTime from,
            OffsetDateTime to
    ) {
        return jdbcTemplate.queryForObject(
                AdminMetricsQuery.PLATFORM_OVERVIEW,
                (rs, rowNum) -> new PlatformOverviewResponse(
                        rs.getLong("totalWatchTimeMs"),
                        rs.getLong("avgWatchDurationMs"),
                        rs.getLong("uniqueSessions"),
                        rs.getLong("uniqueUsers"),
                        rs.getLong("playCount"),
                        rs.getLong("completeCount"),
                        rs.getBigDecimal("completionRate"),
                        rs.getInt("peekConcurrentViewers"),
                        rs.getBigDecimal("avgConcurrentViewers")
                ),
                from,
                to
        );
    }

    @Override
    public List<ContentComparisonResponse> contentComparison(
            List<UUID> contentIds,
            OffsetDateTime from,
            OffsetDateTime to
    ) {
        return jdbcTemplate.query(
                AdminMetricsQuery.CONTENT_COMPARISON,
                (rs, rowNum) -> new ContentComparisonResponse(
                        rs.getObject("contentId", UUID.class),
                        rs.getLong("totalWatchTimeMs"),
                        rs.getLong("avgWatchDurationMs"),
                        rs.getLong("playCount"),
                        rs.getLong("completeCount"),
                        rs.getBigDecimal("completionRate"),
                        rs.getInt("peekConcurrentViewers"),
                        rs.getBigDecimal("avgConcurrentViewers")
                ),
                from,
                to,
                contentIds
        );
    }
}
