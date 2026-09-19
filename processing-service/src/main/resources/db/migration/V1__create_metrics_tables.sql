-- ============================================
-- Metrics Database Schema
-- ============================================

-- --------------------------------------------
-- Watch Time Metrics
-- --------------------------------------------

CREATE TABLE watch_time_metrics (
    content_id UUID NOT NULL,
    window_start TIMESTAMP WITH TIME ZONE NOT NULL,
    window_end TIMESTAMP WITH TIME ZONE NOT NULL,

    total_watch_time_ms BIGINT NOT NULL DEFAULT 0,
    avg_watch_duration_ms BIGINT NOT NULL DEFAULT 0,
    unique_sessions INTEGER NOT NULL DEFAULT 0,
    unique_users INTEGER NOT NULL DEFAULT 0,

    computed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_watch_time_metrics PRIMARY KEY (content_id, window_start, window_end)
);

CREATE INDEX idx_watch_time_metrics_content_window ON watch_time_metrics (content_id, window_start DESC);

-- --------------------------------------------
-- Completion Metrics
-- --------------------------------------------

CREATE TABLE completion_metrics (
    content_id UUID NOT NULL,
    window_start TIMESTAMP WITH TIME ZONE NOT NULL,
    window_end TIMESTAMP WITH TIME ZONE NOT NULL,

    play_count INTEGER NOT NULL DEFAULT 0,
    complete_count INTEGER NOT NULL DEFAULT 0,
    completion_rate NUMERIC(5,2) NOT NULL DEFAULT 0,

    computed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_completion_metrics PRIMARY KEY (content_id, window_start, window_end)
);

CREATE INDEX idx_completion_metrics_content_window ON completion_metrics (content_id, window_start DESC);

-- --------------------------------------------
-- Drop off Heatmap
-- --------------------------------------------

CREATE TABLE dropoff_heatmap (
    content_id UUID NOT NULL,
    window_start TIMESTAMP WITH TIME ZONE NOT NULL,
    window_end TIMESTAMP WITH TIME ZONE NOT NULL,

    position_bucket SMALLINT NOT NULL,
    stop_count INTEGER NOT NULL DEFAULT 0,

    computed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_dropoff_heatmap PRIMARY KEY (content_id, window_start, position_bucket),

    CONSTRAINT chk_position_bucket CHECK (position_bucket BETWEEN 0 AND 9)
);

CREATE INDEX idx_dropoff_heatmap_content_window ON dropoff_heatmap (content_id, window_start DESC);

-- --------------------------------------------
-- Concurrent Viewers Snapshot
-- --------------------------------------------

CREATE TABLE concurrent_viewers_snapshot (
    content_id          UUID NOT NULL,
    window_start        TIMESTAMP WITH TIME ZONE NOT NULL,
    window_end          TIMESTAMP WITH TIME ZONE NOT NULL,

    peak_viewers        INTEGER NOT NULL DEFAULT 0,
    avg_viewers         NUMERIC(10,2) NOT NULL DEFAULT 0,

    computed_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_concurrent_viewers_snapshot PRIMARY KEY (content_id, window_start, window_end)
);

CREATE INDEX idx_concurrent_viewers_content_window ON concurrent_viewers_snapshot (content_id, window_start DESC);
