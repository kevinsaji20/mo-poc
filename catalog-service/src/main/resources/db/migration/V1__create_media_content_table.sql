CREATE TABLE media_content (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    title VARCHAR(255) NOT NULL,
    description TEXT,

    content_type VARCHAR(50) NOT NULL,
    genre VARCHAR(50) NOT NULL,

    language VARCHAR(50),
    duration_seconds INTEGER NOT NULL,

    thumbnail_url TEXT,
    stream_url TEXT,

    release_date DATE,
    channel_name VARCHAR(255),

    status VARCHAR(50) NOT NULL,

    created_by UUID NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_duration
        CHECK(duration_seconds >= 0)
)