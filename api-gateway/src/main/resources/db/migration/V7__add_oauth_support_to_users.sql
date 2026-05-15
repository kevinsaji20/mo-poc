CREATE TYPE authProviderType AS ENUM (
    'LOCAL',
    'GOOGLE'
    );

ALTER TABLE users
    ALTER COLUMN password_hash DROP NOT NULL;

ALTER TABLE users
    ADD COLUMN provider authProviderType NOT NULL DEFAULT 'LOCAL';

ALTER TABLE users
    ADD COLUMN provider_id VARCHAR(255);

ALTER TABLE users
    ADD COLUMN profile_picture TEXT;
