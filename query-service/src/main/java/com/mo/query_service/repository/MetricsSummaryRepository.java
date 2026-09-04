package com.mo.query_service.repository;

import com.mo.query_service.projections.MetricsSummaryProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MetricsSummaryRepository {
    @Query(value = """
    SELECT
        wtm.total_watch_time_ms AS totalWatchTimeMs,
        wtm.avg_watch_duration_ms AS avgWatchDurationMs,
        wtm.unique_sessions AS uniqueSessions,
        wtm.unique_users AS uniqueUsers,
        cm.play_count AS playCount,
        cm.complete_count AS completeCount,
        cv.peak_viewers AS peakViewers,
        cv.avg_viewers AS avgViewers
    FROM (
        SELECT
            COALESCE(SUM(total_watch_time_ms), 0) AS total_watch_time_ms,
            COALESCE(
                    SUM(total_watch_time_ms) / NULLIF(SUM(unique_sessions), 0),
                    0
                ) AS avg_watch_duration_ms,
            COALESCE(SUM(unique_sessions), 0) AS unique_sessions,
            COALESCE(SUM(unique_users), 0) AS unique_users
        FROM watch_time_metrics
        WHERE content_id = :contentId
            AND window_start >= :from
            AND window_end <= :to
    ) AS wtm
    CROSS JOIN (
        SELECT
            COALESCE(SUM(play_count), 0) AS play_count,
            COALESCE(SUM(complete_count), 0) AS complete_count
        FROM completion_metrics
        WHERE content_id = :contentId
            AND window_start >= :from
            AND window_end <= :to
    ) cm
    CROSS JOIN (
        SELECT
            COALESCE(MAX(peak_viewers), 0) AS peak_viewers,
            COALESCE(AVG(avg_viewers), 0) AS avg_viewers
        FROM concurrent_viewers_snapshot
        WHERE content_id = :contentId
            AND window_start >= :from
            AND window_end <= :to
    ) AS cv
    """, nativeQuery = true)
    MetricsSummaryProjection getSummary(
            @Param("contentId") UUID contentId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );
}
