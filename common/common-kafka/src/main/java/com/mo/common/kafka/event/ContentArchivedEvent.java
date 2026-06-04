package com.mo.common.kafka.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ContentArchivedEvent(
        UUID contentId,
        OffsetDateTime archivedAt
) {
}
