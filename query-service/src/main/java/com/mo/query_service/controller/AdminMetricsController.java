package com.mo.query_service.controller;

import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.dto.response.ContentComparisonResponse;
import com.mo.query_service.dto.response.PlatformOverviewResponse;
import com.mo.query_service.service.AdminMetricsService;
import com.mo.query_service.web.ValidMetricQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/metrics")
@RequiredArgsConstructor
public class AdminMetricsController {
    private final AdminMetricsService adminMetricsService;

    @GetMapping("/platform-overview")
    @PreAuthorize("hasRole('ANALYTICS_ADMIN')")
    public ResponseEntity<PlatformOverviewResponse> getPlatformOverview(
            @ValidMetricQuery MetricsQueryRequest queryParams
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminMetricsService.getPlatformOverview(queryParams));
    }

    @GetMapping("/content-comparison")
    @PreAuthorize("hasRole('ANALYTICS_ADMIN')")
    public ResponseEntity<List<ContentComparisonResponse>> contentComparison(
            @RequestParam List<UUID> contentIds,
            @ValidMetricQuery MetricsQueryRequest queryParams
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminMetricsService.contentComparison(contentIds, queryParams));
    }
}
