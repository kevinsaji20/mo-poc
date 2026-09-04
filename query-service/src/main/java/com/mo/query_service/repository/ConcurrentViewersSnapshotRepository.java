package com.mo.query_service.repository;

import com.mo.query_service.entity.ConcurrentViewersSnapshot;
import com.mo.query_service.entity.ConcurrentViewersSnapshotId;
import com.mo.query_service.projections.ConcurrentViewersProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ConcurrentViewersSnapshotRepository
        extends JpaRepository<ConcurrentViewersSnapshot, ConcurrentViewersSnapshotId> {
    @Query(value = """
        SELECT
            date_trunc(:granularity, window_start) AS bucket,
            MAX(peak_viewers) AS peakViewers,
            AVG(avg_viewers) AS avgViewers
        FROM concurrent_viewers_snapshot
        WHERE content_id = :contentId
          AND window_start >= :from
          AND window_end <= :to
        GROUP BY date_trunc(:granularity, window_start)
        ORDER BY bucket
        """, nativeQuery = true)
    List<ConcurrentViewersProjection> findConcurrentViewersTrend(
            @Param("contentId") UUID contentId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("granularity") String granularity
    );
}
