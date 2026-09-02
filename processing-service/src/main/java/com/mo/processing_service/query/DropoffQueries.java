package com.mo.processing_service.query;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DropoffQueries {
    public static final String UPSERT = """
            INSERT INTO dropoff_heatmap (
                content_id,
                window_start,
                window_end,
                position_bucket,
                stop_count,
                computed_at
            )
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (content_id, window_start, position_bucket)
            DO UPDATE SET
                window_end = EXCLUDED.window_end,
                stop_count = EXCLUDED.stop_count,
                computed_at = EXCLUDED.computed_at
            ;
            """;
}
