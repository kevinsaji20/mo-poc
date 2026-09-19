package com.mo.query_service.repository;

import com.mo.query_service.dto.response.SummaryResponse;
import com.mo.query_service.repository.query.MetricsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MetricsSummaryRepositoryImpl implements MetricsSummaryRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public SummaryResponse getSummary(
            UUID contentId,
            OffsetDateTime from,
            OffsetDateTime to
    ) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("contentId", contentId)
                .addValue("from", from)
                .addValue("to", to);
        return jdbcTemplate.queryForObject(
                MetricsQuery.SUMMARY_QUERY,
                params,
                (rs, rowNum)-> new SummaryResponse(
                        rs.getLong("totalWatchTimeMs"),
                        rs.getLong("avgWatchDurationMs"),
                        rs.getLong("uniqueSessions"),
                        rs.getLong("uniqueUsers"),
                        rs.getLong("playCount"),
                        rs.getLong("completeCount"),
                        rs.getBigDecimal("completionRate"),
                        rs.getInt("peakViewers"),
                        rs.getBigDecimal("avgViewers")
                )
        );
    }
}
