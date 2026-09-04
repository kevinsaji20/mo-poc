package com.mo.query_service.controller;

import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.dto.response.CompletionResponse;
import com.mo.query_service.dto.response.SummaryResponse;
import com.mo.query_service.dto.response.WatchTimeResponse;
import com.mo.query_service.service.MetricsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/metrics/{contentId}")
@RequiredArgsConstructor
public class MetricsController {
    private final MetricsService metricsService;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ANALYTICS_READ')")
    public ResponseEntity<SummaryResponse> getSummaryByContentId(
            @PathVariable UUID contentId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SummaryResponse());
    }

    @GetMapping("/watch-time")
    @PreAuthorize("hasRole('ANALYTICS_READ')")
    public ResponseEntity<List<WatchTimeResponse>> getWatchTime(
            @PathVariable UUID contentId,
            @Valid @ModelAttribute MetricsQueryRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        metricsService.getWatchTime(contentId, request)
                );
    }

    @GetMapping("/completion")
    @PreAuthorize("hasRole('ANALYTICS_READ')")
    public ResponseEntity<List<CompletionResponse>> getCompletion(
            @PathVariable UUID contentId,
            @Valid @ModelAttribute MetricsQueryRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        metricsService.getCompletion(contentId, request)
                );
    }

}
