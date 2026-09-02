package com.mo.processing_service.query;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConcurrentViewerQueries {
    public static final String UPSERT = """
            INSERT INTO concurrent_viewers_snapshot (
                content_id,
                window_start,
                window_end,
                peak_viewers,
                avg_viewers,
                computed_at
            )
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (content_id, window_start, window_end)
            DO UPDATE SET
                peak_viewers = EXCLUDED.peak_viewers,
                avg_viewers = EXCLUDED.avg_viewers,
                computed_at = EXCLUDED.computed_at
            ;
            """;
}
