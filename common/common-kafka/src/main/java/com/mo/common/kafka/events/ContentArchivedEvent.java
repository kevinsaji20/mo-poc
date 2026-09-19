package com.mo.common.kafka.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ContentArchivedEvent(
        UUID contentId,
        OffsetDateTime archivedAt
) {
}
