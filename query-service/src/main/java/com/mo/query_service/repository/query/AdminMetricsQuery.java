package com.mo.query_service.repository.query;

public class AdminMetricsQuery {
    public static final String PLATFORM_OVERVIEW = """
        SELECT
            COALESCE(wtm.total_watch_time_ms, 0) AS "totalWatchTimeMs",
            COALESCE(wtm.avg_watch_duration_ms, 0) AS "avgWatchDurationMs",
            COALESCE(wtm.unique_sessions, 0) AS "uniqueSessions",
            COALESCE(wtm.unique_users, 0) AS "uniqueUsers",
            COALESCE(cm.play_count, 0) AS "playCount",
            COALESCE(cm.complete_count, 0) AS "completeCount",
            CASE
                WHEN COALESCE(cm.play_count, 0) THEN 0
                ELSE COALESCE(cm.complete_count, 0)::NUMERIC / cm.play_count
            END AS "completionRate",
            COALESCE(cv.peak_concurrent_viewers, 0) AS "peakConcurrentViewers",
            COALESCE(cv.avg_concurrent_viewers, 0) AS "avgConcurrentViewers"
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
            WHERE window_start >= $1
                AND window_end <= $2
        ) AS wtm
        CROSS JOIN (
            SELECT
                SUM(play_count) AS play_count,
                SUM(complete_count) AS complete_count
            FROM completion_metrics
            WHERE window_start >= $1
                AND window_end <= $2
        ) AS cm
        CROSS JOIN (
            SELECT
                MAX(peak_viewers) AS peak_concurrent_viewers,
                AVG(avg_viewers) AS avg_concurrent_viewers
            FROM concurrent_viewers_snapshot
            WHERE window_start >= $1
                AND window_end <= $2
        ) AS cv
    """;

    public static final String CONTENT_COMPARISON = """
        SELECT
            ids.content_id AS "contentId",
            COALESCE(wtm.total_watch_time_ms, 0) AS "totalWatchTimeMs",
            COALESCE(wtm.avg_watch_duration_ms, 0) AS "avgWatchDurationMs",
            COALESCE(cm.play_count, 0) AS "playCount",
            COALESCE(cm.complete_count, 0) AS "completeCount",
            CASE
                WHEN COALESCE(cm.play_count, 0) THEN 0
                ELSE COALESCE(cm.complete_count, 0)::NUMERIC / cm.play_count
            END AS "completionRate",
            COALESCE(cv.peak_concurrent_viewers, 0) AS "peakConcurrentViewers",
            COALESCE(cv.avg_concurrent_viewers, 0) AS "avgConcurrentViewers"
        FROM (
            SELECT unnest(CAST(:$3 AS UUID[])) AS content_id
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
            WHERE window_start >= $1
                AND window_end <= $2
            GROUP BY content_id
        ) AS wtm
        ON wtm.content_id = ids.content_id
        LEFT JOIN (
            SELECT
                content_id,
                SUM(play_count) AS play_count,
                SUM(complete_count) AS complete_count
            FROM completion_metrics
            WHERE window_start >= $1
                AND window_end <= $2
            GROUP BY content_id
        ) AS cm
        ON cm.content_id = ids.content_id
        LEFT JOIN (
            SELECT
                content_id,
                MAX(peak_viewers) AS peak_concurrent_viewers,
                AVG(avg_viewers) AS avg_concurrent_viewers
            FROM concurrent_viewers_snapshot
            WHERE window_start >= $1
                AND window_end <= $2
            GROUP BY content_id
        ) AS cv
        ON cv.content_id = ids.content_id
    """;
}
