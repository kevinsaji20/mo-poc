package com.mo.query_service.repository;

import com.mo.query_service.projections.PlatformOverviewProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AdminMetricsRepository {
    @Query(value = """
        SELECT
            wtm.total_watch_time_ms AS totalWatchTimeMs,
            wtm.avg_watch_duration_ms AS avgWatchDurationMs,
            wtm.unique_sessions AS uniqueSessions,
            wtm.unique_users AS uniqueUsers,
            cm.play_count AS playCount,
            cm.complete_count AS completeCount,
            cv.peak_concurrent_viewers AS peakConcurrentViewers,
            cv.avg_concurrent_viewers AS avgConcurrentViewers
        FROM (
            SELECT
                COALESCE(SUM(total_watch_time_ms), 0) AS total_watch_time_ms,
                CASE
                    WHEN SUM(unique_sessions) = 0 THEN 0
                    ELSE SUM(total_watch_time_ms) / SUM(unique_sessions)
                END AS avg_watch_duration_ms,
                COALESCE(SUM(unique_sessions), 0) AS unique_sessions,
                COALESCE(SUM(unique_users), 0) AS unique_users
            FROM watch_time_metrics
            WHERE window_start >= :from
                AND window_end <= :to
        ) AS wtm
        CROSS JOIN (
            SELECT
                COALESCE(SUM(play_count), 0) AS play_count,
                COALESCE(SUM(complete_count), 0) AS complete_count
            FROM completion_metrics
            WHERE window_start >= :from
                AND window_end <= :to
        ) AS cm
        CROSS JOIN (
            SELECT 
                COALESCE(MAX(peak_viewers), 0) AS peak_concurrent_viewers,
                COALESCE(AVG(avg_viewers), 0) AS avg_concurrent_viewers
            FROM concurrent_viewers_snapshot
            WHERE window_start >= :from
                AND window_end <= :to
        ) AS cv
    """, nativeQuery = true)
    PlatformOverviewProjection getPlatformOverView(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );
}
