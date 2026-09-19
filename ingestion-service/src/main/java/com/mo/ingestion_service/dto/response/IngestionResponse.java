package com.mo.ingestion_service.dto.response;

import com.mo.ingestion_service.enums.IngestionStatus;

import java.util.UUID;

public record IngestionResponse (
        IngestionStatus status,
        UUID eventId
) {
}
