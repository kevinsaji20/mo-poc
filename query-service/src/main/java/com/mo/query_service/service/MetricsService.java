package com.mo.query_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mo.query_service.cache.CacheTTLStrategy;
import com.mo.query_service.cache.MetricsCacheKeys;
import com.mo.query_service.cache.MetricsCacheService;
import com.mo.query_service.client.CatalogClient;
import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.dto.response.*;
import com.mo.query_service.exception.ContentNotFoundException;
import com.mo.query_service.projections.*;
import com.mo.query_service.repository.*;
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
    private final DropoffHeatmapRepository dropoffHeatmapRepository;
    private final ConcurrentViewersSnapshotRepository concurrentViewersSnapshotRepository;
    private final MetricsSummaryRepository metricsSummaryRepository;

    private final MetricsCacheService metricsCacheService;
    private final CacheTTLStrategy cacheTTLStrategy;


    public SummaryResponse getSummary(UUID contentId, MetricsQueryRequest queryParams) {
        if (!catalogClient.contentExists(contentId)) {
            throw new ContentNotFoundException();
        }

        // 1 Try Redis
        String key = MetricsCacheKeys.summary(contentId, queryParams);

        SummaryResponse cached = metricsCacheService.get(key, SummaryResponse.class);
        if (cached != null) {
            return cached;
        }

        // 2 Cache miss -> PostgreSQL
        MetricsSummaryProjection projection =
                metricsSummaryRepository.getSummary(
                        contentId,
                        queryParams.from(),
                        queryParams.to()
                );

        BigDecimal completionRate =
                projection.getPlayCount() == 0
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(projection.getCompleteCount())
                          .divide(BigDecimal.valueOf(projection.getPlayCount()), 4, RoundingMode.HALF_UP)
                          .multiply(BigDecimal.valueOf(100));

        SummaryResponse response = new SummaryResponse(
                projection.getTotalWatchTimeMs(),
                projection.getAvgWatchDurationMs(),
                projection.getUniqueSessions(),
                projection.getUniqueUsers(),
                projection.getPlayCount(),
                projection.getCompleteCount(),
                completionRate,
                projection.getPeakViewers(),
                projection.getAvgViewers()
        );

        // 3 Store in Redis
        metricsCacheService.put(key, response, cacheTTLStrategy.contentMetricsTtl(queryParams.to()));

        return response;
    }

    public List<WatchTimeResponse> getWatchTime(UUID contentId, MetricsQueryRequest queryParams) {
        if(!catalogClient.contentExists(contentId)) {
            throw new ContentNotFoundException();
        }
        // 1 Try Redis
        String key = MetricsCacheKeys.watchTime(contentId, queryParams);
        List<WatchTimeResponse> cached =
                metricsCacheService.get(
                        key,
                        new TypeReference<List<WatchTimeResponse>>() {}
                );
        if (cached != null) {
            return cached;
        }

        // 2 Cache miss -> PostgreSQL
        List<WatchTimeProjection> projections =
                watchTimeMetricsRepository
                        .findWatchTime(
                                contentId,
                                queryParams.from(),
                                queryParams.to(),
                                queryParams.granularity().name()
                        );

        List<WatchTimeResponse>  response = projections.stream()
                .map(projection -> new WatchTimeResponse(
                        projection.getBucket(),
                        projection.getTotalWatchTimeMs(),
                        projection.getUniqueSessions(),
                        projection.getUniqueUsers()
                ))
                .toList();

        // 3 Store in Redis
        metricsCacheService.put(key, response, cacheTTLStrategy.contentMetricsTtl(queryParams.to()));

        return response;
    }

    public List<CompletionResponse> getCompletion(UUID contentId, MetricsQueryRequest queryParams) {
        if(!catalogClient.contentExists(contentId)) {
            throw new ContentNotFoundException();
        }

        // 1 Try Redis
        String key = MetricsCacheKeys.completion(contentId, queryParams);
        List<CompletionResponse> cached =
                metricsCacheService.get(
                        key,
                        new TypeReference<List<CompletionResponse>>() {}
                );
        if (cached != null) {
            return cached;
        }

        // 2 Cache miss -> PostgreSQL
        List<CompletionProjection> projections =
                completionMetricsRepository.findCompletion(
                        contentId,
                        queryParams.from(),
                        queryParams.to(),
                        queryParams.granularity().name()
                );

        List<CompletionResponse> response = projections.stream()
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

        // 3 Store in Redis
        metricsCacheService.put(key, response, cacheTTLStrategy.contentMetricsTtl(queryParams.to()));

        return response;
    }

    public List<DropoffResponse> getDropoff(UUID contentId, MetricsQueryRequest queryParams) {
        if(!catalogClient.contentExists(contentId)) {
            throw new ContentNotFoundException();
        }
        // 1 Try Redis
        String key = MetricsCacheKeys.dropoff(contentId, queryParams);
        List<DropoffResponse> cached =
                metricsCacheService.get(
                        key,
                        new TypeReference<List<DropoffResponse>>() {}
                );
        if (cached != null) {
            return cached;
        }

        // 2 Cache miss -> PostgreSQL
        List<DropoffProjection> projections =
                dropoffHeatmapRepository.findDropoff(
                        contentId,
                        queryParams.from(),
                        queryParams.to()
                );
        List<DropoffResponse> response = projections.stream()
                .map(projection -> new DropoffResponse(
                        projection.getPostionBucket(),
                        projection.getStopCount()
                ))
                .toList();

        // 3 Store in Redis
        metricsCacheService.put(key, response, cacheTTLStrategy.contentMetricsTtl(queryParams.to()));

        return response;
    }

    public List<ConcurrentViewersResponse> getConcurrentViewers(
            UUID contentId,
            MetricsQueryRequest queryParams
    ) {
        if (!catalogClient.contentExists(contentId)) {
            throw new ContentNotFoundException();
        }
        // 1 Try Redis
        String key = MetricsCacheKeys.concurrent(contentId, queryParams);
        List<ConcurrentViewersResponse> cached =
                metricsCacheService.get(
                        key,
                        new TypeReference<List<ConcurrentViewersResponse>>() {}
                );
        if (cached != null) {
            return cached;
        }

        // 2 Cache miss -> PostgreSQL

        List<ConcurrentViewersProjection> projections =
                concurrentViewersSnapshotRepository.findConcurrentViewersTrend(
                        contentId,
                        queryParams.from(),
                        queryParams.to(),
                        queryParams.granularity().name()
                );

        List<ConcurrentViewersResponse> response = projections.stream()
                .map(projection -> new ConcurrentViewersResponse(
                        projection.getBucket(),
                        projection.getPeakViewers(),
                        projection.getAvgViewers()
                ))
                .toList();

        // 3 Store in Redis
        metricsCacheService.put(key, response, cacheTTLStrategy.contentMetricsTtl(queryParams.to()));

        return response;
    }
}
