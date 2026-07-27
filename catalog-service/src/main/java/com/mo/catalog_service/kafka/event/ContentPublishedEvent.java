package com.mo.catalog_service.kafka.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ContentPublishedEvent(
        UUID contentId,
        String contentType,
        String title,
        OffsetDateTime publishedAt
) {
}
