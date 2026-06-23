package com.mo.ingestion_service.controller;

import com.mo.ingestion_service.dto.request.BackEngagementIngestionRequest;
import com.mo.ingestion_service.dto.request.EngagementIngestionRequest;
import com.mo.ingestion_service.dto.response.IngestionResponse;
import com.mo.ingestion_service.enums.IngestionStatus;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class IngestionController {
    @PostMapping
    @PreAuthorize("hasRole('EVENT_INGEST')")
    public IngestionResponse submitIngestion(
            @Valid @RequestBody EngagementIngestionRequest request
    ) {
        return new IngestionResponse(
                IngestionStatus.ACCEPTED,
                request.eventId()
        );
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('EVENT_INGEST')")
    public Object submitBatch(
            @Valid @RequestBody BackEngagementIngestionRequest request
    ) {
        return request;
    }
}
