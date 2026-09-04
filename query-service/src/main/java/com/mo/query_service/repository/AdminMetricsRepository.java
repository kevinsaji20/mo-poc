package com.mo.query_service.repository;

import com.mo.query_service.projections.ContentComparisonProjection;
import com.mo.query_service.projections.PlatformOverviewProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AdminMetricsRepository {
    @Query(value = """
        SELECT
            COALESCE(wtm.total_watch_time_ms, 0) AS totalWatchTimeMs,
            COALESCE(wtm.avg_watch_duration_ms, 0) AS avgWatchDurationMs,
            COALESCE(wtm.unique_sessions, 0) AS uniqueSessions,
            COALESCE(wtm.unique_users, 0) AS uniqueUsers,
            COALESCE(cm.play_count, 0) AS playCount,
            COALESCE(cm.complete_count, 0) AS completeCount,
            COALESCE(cv.peak_concurrent_viewers, 0) AS peakConcurrentViewers,
            COALESCE(cv.avg_concurrent_viewers, 0) AS avgConcurrentViewers
        FROM (
            SELECT
                SUM(total_watch_time_ms)AS total_watch_time_ms,
                CASE
                    WHEN SUM(unique_sessions) = 0 THEN 0
                    ELSE SUM(total_watch_time_ms) / SUM(unique_sessions)
                END AS avg_watch_duration_ms,
                SUM(unique_sessions) AS unique_sessions,
                SUM(unique_users) AS unique_users
            FROM watch_time_metrics
            WHERE window_start >= :from
                AND window_end <= :to
        ) AS wtm
        CROSS JOIN (
            SELECT
                SUM(play_count) AS play_count,
                SUM(complete_count) AS complete_count
            FROM completion_metrics
            WHERE window_start >= :from
                AND window_end <= :to
        ) AS cm
        CROSS JOIN (
            SELECT
                MAX(peak_viewers) AS peak_concurrent_viewers,
                AVG(avg_viewers) AS avg_concurrent_viewers
            FROM concurrent_viewers_snapshot
            WHERE window_start >= :from
                AND window_end <= :to
        ) AS cv
    """, nativeQuery = true)
    PlatformOverviewProjection getPlatformOverView(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );

    @Query(value = """
        SELECT
            ids.content_id AS contentId,
            COALESCE(wtm.total_watch_time_ms, 0) AS totalWatchTimeMs,
            COALESCE(wtm.avg_watch_duration_ms, 0) AS avgWatchDurationMs,
            COALESCE(cm.play_count, 0) AS playCount,
            COALESCE(cm.complete_count, 0) AS completeCount,
            COALESCE(cv.peak_concurrent_viewers, 0) AS peakConcurrentViewers,
            COALESCE(cv.avg_concurrent_viewers, 0) AS avgConcurrentViewers
        FROM (
            SELECT unnest(CAST(:contentIds AS UUID[])) AS content_id
        ) AS ids
        LEFT JOIN (
            SELECT
                content_id,
                SUM(total_watch_time_ms) AS total_watch_time_ms,
                CASE
                    WHEN SUM(unique_sessions) = 0 THEN 0
                    ELSE SUM(total_watch_time_ms) / SUM(unique_sessions)
                END AS avg_watch_duration_ms
            FROM watch_time_metrics
            WHERE window_start >= :from
                AND window_end <= :to
            GROUP BY content_id
        ) AS wtm
        ON wtm.content_id = ids.content_id
        LEFT JOIN (
            SELECT
                content_id,
                SUM(play_count) AS play_count,
                SUM(complete_count) AS complete_count
            FROM completion_metrics
            WHERE window_start >= :from
                AND window_end <= :to
            GROUP BY content_id 
        ) AS cm
        ON cm.content_id = ids.content_id
        LEFT JOIN (
            SELECT 
                content_id,
                MAX(peak_viewers) AS peak_concurrent_viewers,
                AVG(avg_viewers) AS avg_concurrent_viewers
            FROM concurrent_viewers_snapshot
            WHERE window_start >= :from
                AND window_end <= :to
            GROUP BY content_id 
        ) AS cv
        ON cv.content_id = ids.content_id
    """, nativeQuery = true)
    List<ContentComparisonProjection> contentComparison(
            @Param("contentIds") List<UUID> contentIds,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );
}
