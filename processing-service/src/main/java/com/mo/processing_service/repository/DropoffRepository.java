package com.mo.processing_service.repository;

import com.mo.processing_service.entity.DropoffHeatmap;
import com.mo.processing_service.query.DropoffQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DropoffRepository {
    private final JdbcTemplate jdbcTemplate;

    public void batchUpsert(List<DropoffHeatmap> metrics) {
        jdbcTemplate.batchUpdate(
                DropoffQueries.UPSERT,
                metrics,
                metrics.size(),
                (PreparedStatement ps, DropoffHeatmap metric) -> {
                    ps.setObject(1, metric.contentId());
                    ps.setObject(2, metric.windowStart());
                    ps.setObject(3, metric.windowEnd());
                    ps.setObject(4, metric.positionBucket());
                    ps.setObject(5, metric.stopCount());
                    ps.setObject(6, metric.computedAt());
                }
        );
    }
}
