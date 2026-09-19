package com.mo.ingestion_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BackEngagementIngestionRequest(
        @Valid
        @NotEmpty
        @Size(max = 50)
        List<EngagementIngestionRequest> events
) {
}
