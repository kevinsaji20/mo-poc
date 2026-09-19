package com.mo.ingestion_service.controller;

import com.mo.ingestion_service.dto.request.BackEngagementIngestionRequest;
import com.mo.ingestion_service.dto.request.EngagementIngestionRequest;
import com.mo.ingestion_service.dto.response.IngestionResponse;
import com.mo.ingestion_service.service.IngestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class IngestionController {
    private final IngestionService ingestionService;

    @PostMapping
    @PreAuthorize("hasRole('EVENT_INGEST')")
    public ResponseEntity<IngestionResponse> submitIngestion(
            @Valid @RequestBody EngagementIngestionRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ingestionService.ingest(request));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('EVENT_INGEST')")
    public ResponseEntity<List<IngestionResponse>> submitBatch(
            @Valid @RequestBody BackEngagementIngestionRequest request
    ) {
        List<IngestionResponse> responses = request.events()
                .stream()
                .map(ingestionService::ingest)
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responses);
    }
}
