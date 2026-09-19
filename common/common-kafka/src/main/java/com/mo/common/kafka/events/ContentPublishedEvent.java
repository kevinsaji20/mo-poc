package com.mo.common.kafka.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ContentPublishedEvent(
        UUID contentId,
        String contentType,
        String title,
        OffsetDateTime publishedAt
) {
}
