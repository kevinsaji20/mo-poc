package com.mo.query_service.controller;

import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.dto.response.GenreTrendResponse;
import com.mo.query_service.dto.response.MostCompletedResponse;
import com.mo.query_service.dto.response.TopContentResponse;
import com.mo.query_service.service.TrendsService;
import com.mo.query_service.web.ValidMetricQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trends")
@RequiredArgsConstructor
public class TrendsController {
    private final TrendsService trendsService;

    @GetMapping("/top-content")
    @PreAuthorize("hasRole('ANALYTICS_READ'")
    public ResponseEntity<List<TopContentResponse>> getTopContent(
            @ValidMetricQuery MetricsQueryRequest queryParams
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        trendsService.getTopContent(queryParams)
                );
    }

    @GetMapping("/most-completed")
    @PreAuthorize("hasRole('ANALYTICS_READ'")
    public ResponseEntity<List<MostCompletedResponse>> getMostCompleted(
            @ValidMetricQuery MetricsQueryRequest queryParams
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        trendsService.getMostCompleted(queryParams)
                );
    }

    @GetMapping("/genre/{genre}")
    @PreAuthorize("hasRole('ANALYTICS_READ'")
    public ResponseEntity<List<GenreTrendResponse>> getGenreTrend(
            @PathVariable String genre,
            @ValidMetricQuery MetricsQueryRequest queryParams
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        trendsService.getGenreTrend(genre, queryParams)
                );
    }
}
