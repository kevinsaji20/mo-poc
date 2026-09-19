package com.mo.processing_service.repository;

import com.mo.processing_service.entity.ConcurrentViewerSnapshot;
import com.mo.processing_service.query.ConcurrentViewerQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcurrentViewerRepository {
    private final JdbcTemplate jdbcTemplate;

    public void batchUpsert(List<ConcurrentViewerSnapshot> metrics) {
        jdbcTemplate.batchUpdate(
                ConcurrentViewerQueries.UPSERT,
                metrics,
                metrics.size(),
                (PreparedStatement ps, ConcurrentViewerSnapshot metric) -> {
                    ps.setObject(1, metric.contentId());
                    ps.setObject(2, metric.windowStart());
                    ps.setObject(3, metric.windowEnd());
                    ps.setObject(4, metric.peakViewers());
                    ps.setObject(5, metric.avgViewers());
                    ps.setObject(6, metric.computedAt());
                }
        );
    }
}
