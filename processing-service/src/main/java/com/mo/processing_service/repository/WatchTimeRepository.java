package com.mo.processing_service.repository;

import com.mo.processing_service.entity.WatchTimeMetric;
import com.mo.processing_service.query.WatchTimeQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WatchTimeRepository {
    private final JdbcTemplate jdbcTemplate;

    public void batchUpsert(List<WatchTimeMetric> metrics) {
        jdbcTemplate.batchUpdate(
                WatchTimeQueries.UPSERT,
                metrics,
                metrics.size(),
                (PreparedStatement ps, WatchTimeMetric metric) -> {
                    ps.setObject(1, metric.contentId());
                    ps.setObject(2, metric.windowStart());
                    ps.setObject(3, metric.windowEnd());
                    ps.setObject(4, metric.totalWatchTimeMs());
                    ps.setObject(5, metric.avgWatchDurationMs());
                    ps.setObject(6, metric.uniqueSessions());
                    ps.setObject(7, metric.uniqueUsers());
                    ps.setObject(8, metric.computedAt());
                }
        );
    }
}
