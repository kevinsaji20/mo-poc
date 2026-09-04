package com.mo.query_service.repository;

import com.mo.query_service.entity.WatchTimeMetrics;
import com.mo.query_service.entity.WatchTimeMetricsId;
import com.mo.query_service.projections.GenreTrendProjection;
import com.mo.query_service.projections.TopContentProjection;
import com.mo.query_service.projections.WatchTimeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WatchTimeMetricsRepository
        extends JpaRepository<WatchTimeMetrics, WatchTimeMetricsId> {
    @Query(value = """
            SELECT
                date_trunc(:granulity, window_start) AS bucket,
                SUM(total_watch_time_ms) AS totalWatchTimeMs,
                SUM(unique_sessions) AS uniqueSessions,
                SUM(unique_users) AS uniqueUsers
            FROM watch_time_metrics
            WHERE content_id = :contentId
                AND window_start >= :from
                AND window_end <= :to
            GROUP BY date_trunc(:granularity, window_start)
            ORDER BY bucket
        """, nativeQuery = true)
    public List<WatchTimeProjection> findWatchTime(
            @Param("contentId") UUID contentId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("granularity") String granularity
    );

    @Query(value = """
    SELECT
        content_id AS contentId,
        SUM(total_watch_time_ms) AS totalWacthTimeMs
    FROM watch_time_metrics
    WHERE window_start >= :from
        AND window_end <= :to
    GROUP BY content_id
    ORDER BY totalWacthTimeMs DESC
    LIMIT :size
    OFFSET :offset
    """, nativeQuery = true)
    List<TopContentProjection> getTopContent(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("size") int size,
            @Param("offset") int offset
    );

    @Query(value = """
        SELECT
            content_id AS contentId,
            SUM(total_watch_time_ms) AS totalWatchTimeMs
        FROM watch_time_metrics
        WHERE content_id IN (:contentIds)
          AND window_start >= :from
          AND window_end <= :to
        GROUP BY content_id
        ORDER BY totalWatchTimeMs DESC
        """, nativeQuery = true)
    List<GenreTrendProjection> findGenreTrend(
            @Param("contentIds") List<UUID> contentIds,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );
}
