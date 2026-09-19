CREATE TYPE userStatusType AS ENUM ('ACTIVE', 'LOCKED', 'DISABLED', 'PENDING');

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,

    full_name VARCHAR(255),
    phone_number VARCHAR(20),

    status userStatusType NOT NULL DEFAULT 'ACTIVE',

    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,

    failed_login_attempts INT NOT NULL DEFAULT 0,

    last_login_at TIMESTAMP,
    password_changed_at TIMESTAMP,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE
);