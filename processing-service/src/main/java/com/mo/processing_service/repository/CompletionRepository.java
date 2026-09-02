package com.mo.processing_service.repository;

import com.mo.processing_service.entity.CompletionMetric;
import com.mo.processing_service.query.CompletionQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CompletionRepository {
    private final JdbcTemplate jdbcTemplate;

    public void batchUpsert(List<CompletionMetric> metrics) {
        jdbcTemplate.batchUpdate(
                CompletionQueries.UPSERT,
                metrics,
                metrics.size(),
                (PreparedStatement ps, CompletionMetric metric) -> {
                    ps.setObject(1, metric.contentId());
                    ps.setObject(2, metric.windowStart());
                    ps.setObject(3, metric.windowEnd());
                    ps.setObject(4, metric.playCount());
                    ps.setObject(5, metric.completeCount());
                    ps.setObject(6, metric.completionRate());
                    ps.setObject(7, metric.computedAt());
                }
        );
    }
}
