package com.mo.catalog_service.kafka.event;

import com.mo.catalog_service.enums.ContentType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ContentPublishedEvent(
        UUID contentId,
        ContentType contentType,
        String title,
        OffsetDateTime publishedAt
) {
}
