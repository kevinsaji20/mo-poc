package com.mo.query_service.repository.query;

public class MetricsQuery {
    public static final String SUMMARY_QUERY = """
        SELECT
            wtm.total_watch_time_ms AS "totalWatchTimeMs",
            wtm.avg_watch_duration_ms AS "avgWatchDurationMs",
            wtm.unique_sessions AS "uniqueSessions",
            wtm.unique_users AS "uniqueUsers",
            cm.play_count AS "playCount",
            cm.complete_count AS "completeCount",
            CASE
                WHEN COALESCE(cm.play_count, 0) THEN 0
                ELSE COALESCE(cm.complete_count, 0)::NUMERIC / cm.play_count
            END AS "completionRate",
            cv.peak_viewers AS "peakViewers",
            cv.avg_viewers AS "avgViewers"
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
            WHERE content_id = $1
                AND window_start >= $2
                AND window_end <= $3
        ) AS wtm
        CROSS JOIN (
            SELECT
                COALESCE(SUM(play_count), 0) AS play_count,
                COALESCE(SUM(complete_count), 0) AS complete_count
            FROM completion_metrics
            WHERE content_id = $1
                AND window_start >= $2
                AND window_end <= $3
        ) AS cm
        CROSS JOIN (
            SELECT
                COALESCE(MAX(peak_viewers), 0) AS peak_viewers,
                COALESCE(AVG(avg_viewers), 0) AS avg_viewers
            FROM concurrent_viewers_snapshot
            WHERE content_id = $1
                AND window_start >= $2
                AND window_end <= $3
        ) AS cv
    """;
}
