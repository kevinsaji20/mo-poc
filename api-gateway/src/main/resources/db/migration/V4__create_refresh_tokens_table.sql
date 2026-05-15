CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY default gen_random_uuid(),
    user_id UUID NOT NULL,
    token_hash TEXT NOT NULL,

    jwt_id VARCHAR(255) UNIQUE NOT NULL,

    device_id VARCHAR(255),
    device_name VARCHAR(255),

    ip_address VARCHAR(100),
    user_agent TEXT,

    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    revoked_at TIMESTAMP WITH TIME ZONE,

    replaced_by_token UUID,

    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_used_at TIMESTAMP WITH TIME ZONE,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT fk_refresh_tokens_replaced_by FOREIGN KEY (replaced_by_token) REFERENCES refresh_tokens(id) ON DELETE SET NULL
);
