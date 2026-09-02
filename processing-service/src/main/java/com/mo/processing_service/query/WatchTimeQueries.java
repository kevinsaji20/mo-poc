package com.mo.processing_service.query;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WatchTimeQueries {
    public static final String UPSERT = """
            INSERT INTO watch_time_metrics (
                content_id,
                window_start,
                window_end,
                total_watch_time_ms,
                avg_watch_duration_ms,
                unique_sessions,
                unique_users,
                computed_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (content_id, window_start, window_end)
            DO UPDATE SET
                total_watch_time_ms = EXCLUDED.total_watch_time_ms,
                avg_watch_duration_ms = EXCLUDED.avg_watch_duration_ms,
                unique_sessions = EXCLUDED.unique_sessions,
                unique_users = EXCLUDED.unique_users,
                computed_at = EXCLUDED.computed_at
            ;
            """;
}
