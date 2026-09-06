package com.mo.query_service.repository;

import com.mo.query_service.dto.response.SummaryResponse;
import com.mo.query_service.repository.query.MetricsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MetricsSummaryRepositoryImpl implements MetricsSummaryRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public SummaryResponse getSummary(
            UUID contentId,
            OffsetDateTime from,
            OffsetDateTime to
    ) {
        return jdbcTemplate.queryForObject(
                MetricsQuery.SUMMARY_QUERY,
                (rs, rowNum)-> new SummaryResponse(
                        rs.getLong("totalWatchTimeMs"),
                        rs.getLong("avgWatchDurationMs"),
                        rs.getLong("uniqueSessions"),
                        rs.getLong("uniqueUsers"),
                        rs.getLong("playCount"),
                        rs.getLong("completeCount"),
                        rs.getBigDecimal("completionRate"),
                        rs.getInt("peekViewers"),
                        rs.getBigDecimal("avgViewers")
                ),
                contentId,
                from,
                to
        );
    }
}
