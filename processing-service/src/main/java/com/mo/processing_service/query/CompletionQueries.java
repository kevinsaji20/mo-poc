package com.mo.processing_service.query;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompletionQueries {
    public static final String UPSERT = """
            INSERT INTO completion_metrics (
                content_id,
                window_start,
                window_end,
                play_count,
                complete_count,
                completion_rate,
                computed_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (content_id, window_start, window_end)
            DO UPDATE SET
                play_count = EXCLUDED.play_count,
                complete_count = EXCLUDED.complete_count,
                completion_rate = EXCLUDED.completion_rate,
                computed_at = EXCLUDED.computed_at
            ;
            """;
}
