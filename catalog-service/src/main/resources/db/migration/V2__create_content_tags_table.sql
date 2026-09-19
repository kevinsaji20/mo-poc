CREATE TABLE content_tags(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id UUID NOT NULL,
    tag VARCHAR(100) NOT NULL,

    CONSTRAINT fk_content_tags_content FOREIGN KEY(content_id) REFERENCES media_content(id) ON DELETE CASCADE
)