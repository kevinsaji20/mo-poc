package com.mo.query_service.repository;

import com.mo.query_service.dto.response.ContentComparisonResponse;
import com.mo.query_service.dto.response.PlatformOverviewResponse;
import com.mo.query_service.repository.query.AdminMetricsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AdminMetricsRepositoryImpl implements AdminMetricsRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public PlatformOverviewResponse getPlatformOverview(
            OffsetDateTime from,
            OffsetDateTime to
    ) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to);
        return jdbcTemplate.queryForObject(
                AdminMetricsQuery.PLATFORM_OVERVIEW,
                params,
                (rs, rowNum) -> new PlatformOverviewResponse(
                        rs.getLong("totalWatchTimeMs"),
                        rs.getLong("avgWatchDurationMs"),
                        rs.getLong("uniqueSessions"),
                        rs.getLong("uniqueUsers"),
                        rs.getLong("playCount"),
                        rs.getLong("completeCount"),
                        rs.getBigDecimal("completionRate"),
                        rs.getInt("peakConcurrentViewers"),
                        rs.getBigDecimal("avgConcurrentViewers")
                )
        );
    }

    @Override
    public List<ContentComparisonResponse> contentComparison(
            List<UUID> contentIds,
            OffsetDateTime from,
            OffsetDateTime to
    ) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to)
                .addValue("contentIds", contentIds);
        return jdbcTemplate.query(
                AdminMetricsQuery.CONTENT_COMPARISON,
                params,
                (rs, rowNum) -> new ContentComparisonResponse(
                        rs.getObject("contentId", UUID.class),
                        rs.getLong("totalWatchTimeMs"),
                        rs.getLong("avgWatchDurationMs"),
                        rs.getLong("playCount"),
                        rs.getLong("completeCount"),
                        rs.getBigDecimal("completionRate"),
                        rs.getInt("peakConcurrentViewers"),
                        rs.getBigDecimal("avgConcurrentViewers")
                )
        );
    }
}
