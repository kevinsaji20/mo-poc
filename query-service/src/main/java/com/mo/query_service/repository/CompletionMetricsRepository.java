package com.mo.query_service.repository;

import com.mo.query_service.entity.CompletionMetrics;
import com.mo.query_service.entity.CompletionMetricsId;
import com.mo.query_service.projections.CompletionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CompletionMetricsRepository
        extends JpaRepository<CompletionMetrics, CompletionMetricsId> {

    @Query(value = """
    SELECT
        date_trunc(:granularity, window_start) AS bucket,
        sum(play_count) AS playCount,
        sum(complete_count) AS completeCount
    FROM completion_metrics
    WHERE content_id = :contentId
        AND window_start >= :from
        AND window_end <= :to
    GROUP BY date_trunc(:granularity, window_start)
    ORDER BY bucket
    """, nativeQuery = true)
    List<CompletionProjection> findCompletion(
            @Param("contentId") UUID contentId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("granularity") String granularity
    );
}
