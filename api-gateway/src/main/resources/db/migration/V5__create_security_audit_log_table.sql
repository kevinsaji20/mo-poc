CREATE TABLE security_audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID,

    event_type VARCHAR(100) NOT NULL,
    event_status VARCHAR(50) NOT NULL,

    ip_address VARCHAR(100),
    user_agent TEXT,

    device_id VARCHAR(255),

    resource VARCHAR(255),
    http_method VARCHAR(10),

    request_id VARCHAR(255),

    description TEXT,

    metadata JSONB,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_security_audit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);