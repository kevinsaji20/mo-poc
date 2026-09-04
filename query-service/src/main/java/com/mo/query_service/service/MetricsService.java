package com.mo.query_service.service;

import com.mo.query_service.client.CatalogClient;
import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.dto.response.CompletionResponse;
import com.mo.query_service.dto.response.WatchTimeResponse;
import com.mo.query_service.exception.ContentNotFoundException;
import com.mo.query_service.projections.CompletionTrendProjection;
import com.mo.query_service.projections.WatchTimeTrendProjection;
import com.mo.query_service.repository.CompletionMetricsRepository;
import com.mo.query_service.repository.WatchTimeMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final CatalogClient catalogClient;
    private final WatchTimeMetricsRepository watchTimeMetricsRepository;
    private final CompletionMetricsRepository completionMetricsRepository;


    public void getSummaryByContentId(UUID contentId) {

    }

    public List<WatchTimeResponse> getWatchTime(UUID contentId, MetricsQueryRequest request) {
        if(!catalogClient.contentExists(contentId)) {
            throw new ContentNotFoundException();
        }

        String granularity = request.granularity().toString().toLowerCase();

        List<WatchTimeTrendProjection> projections =
                watchTimeMetricsRepository
                        .findWatchTimeTrend(
                            contentId,
                            request.from(),
                            request.to(),
                            granularity
                        );

        return projections.stream()
                .map(projection -> new WatchTimeResponse(
                        projection.getBucket(),
                        projection.getTotalWatchTimeMs(),
                        projection.getUniqueSessions(),
                        projection.getUniqueUsers()
                ))
                .toList();
    }

    public List<CompletionResponse> getCompletion(UUID contentId, MetricsQueryRequest request) {
        if(!catalogClient.contentExists(contentId)) {
            throw new ContentNotFoundException();
        }

        String granularity = request.granularity().toString().toLowerCase();

        List<CompletionTrendProjection> projections =
                completionMetricsRepository.findCompletionTrend(
                        contentId,
                        request.from(),
                        request.to(),
                        granularity
                );

        return projections.stream()
                .map(projection -> {
                    Long playCount = projection.getPlayCount();
                    Long completeCount = projection.getCompleteCount();

                    BigDecimal completionRate =
                            playCount == 0
                                    ? BigDecimal.ZERO
                                    : BigDecimal.valueOf(completeCount)
                                    .divide(
                                            BigDecimal.valueOf(playCount),
                                            4,
                                            RoundingMode.HALF_UP
                                    )
                                    .multiply(HUNDRED);
                    return new CompletionResponse(
                            projection.getBucket(),
                            playCount,
                            completeCount,
                            completionRate
                    );
                })
                .toList();
    }
}
