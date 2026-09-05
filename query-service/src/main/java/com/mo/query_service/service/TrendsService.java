package com.mo.query_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mo.query_service.cache.CacheTTLStrategy;
import com.mo.query_service.cache.MetricsCacheKeys;
import com.mo.query_service.cache.MetricsCacheService;
import com.mo.query_service.client.CatalogClient;
import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.dto.response.*;
import com.mo.query_service.projections.GenreTrendProjection;
import com.mo.query_service.projections.MostCompletedProjection;
import com.mo.query_service.projections.TopContentProjection;
import com.mo.query_service.repository.CompletionMetricsRepository;
import com.mo.query_service.repository.WatchTimeMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrendsService {
    private final WatchTimeMetricsRepository watchTimeMetricsRepository;
    private final CompletionMetricsRepository completionMetricsRepository;

    private final MetricsCacheService metricsCacheService;
    private final CacheTTLStrategy cacheTTLStrategy;

    private final CatalogClient catalogClient;

    public List<TopContentResponse> getTopContent(MetricsQueryRequest queryParams) {
        // 1 Try Redis
        String key = MetricsCacheKeys.topContent(queryParams);

        List<TopContentResponse> cached = metricsCacheService.get(
                key,
                new TypeReference<List<TopContentResponse>>() {}
        );
        if (cached != null) {
            return cached;
        }

        // 2 Cache miss -> PostgreSQL
        List<TopContentProjection> projections =
                watchTimeMetricsRepository.getTopContent(
                        queryParams.from(),
                        queryParams.to(),
                        queryParams.size(),
                        queryParams.offset()
                );

        List<TopContentResponse> response = projections.stream()
                .map(
                        projection -> new TopContentResponse(
                                projection.getContentId(),
                                projection.getTotalWatchTimeMs()
                        )
                )
                .toList();
        // 3 Store in Redis
        metricsCacheService.put(key, response, cacheTTLStrategy.trendingTtl());
        return response;
    }

    public List<MostCompletedResponse> getMostCompleted(MetricsQueryRequest queryParams) {
        // 1 Try Redis
        String key = MetricsCacheKeys.mostCompleted(queryParams);

        List<MostCompletedResponse> cached = metricsCacheService.get(
                key,
                new TypeReference<List<MostCompletedResponse>>() {}
        );
        if (cached != null) {
            return cached;
        }

        // 2 Cache miss -> PostgreSQL
        List<MostCompletedProjection> projections =
                completionMetricsRepository.findMostCompleted(
                        queryParams.from(),
                        queryParams.to(),
                        queryParams.size(),
                        queryParams.offset()
                );

        List<MostCompletedResponse> response = projections.stream()
                .map(projection -> new MostCompletedResponse(
                        projection.getContentId(),
                        projection.getCompleteCount()
                ))
                .toList();
        // 3 Store in Redis
        metricsCacheService.put(key, response, cacheTTLStrategy.trendingTtl());
        return response;
    }

    public List<GenreTrendResponse> getGenreTrend(String genre, MetricsQueryRequest queryParams) {
        // 1 Try Redis
        String key = MetricsCacheKeys.topGenreContent(genre, queryParams);

        List<GenreTrendResponse> cached = metricsCacheService.get(
                key,
                new TypeReference<List<GenreTrendResponse>>() {}
        );
        if (cached != null) {
            return cached;
        }

        List<CatalogResponse> contents = catalogClient.getContentByGenre(genre);
        if(contents == null) {
            metricsCacheService.put(key, List.of(), cacheTTLStrategy.trendingTtl());
            return List.of();
        }

        Map<UUID, CatalogResponse> contentById = contents.stream()
                .collect(Collectors.toMap(CatalogResponse::contentId, Function.identity()));

        List<UUID> contentIds = contents.stream().map(CatalogResponse::contentId).toList();

        List<GenreTrendProjection> projections =
                watchTimeMetricsRepository.findGenreTrend(
                        contentIds,
                        queryParams.from(),
                        queryParams.to()
                );

        List<GenreTrendResponse> response = projections.stream()
                .map(projection -> {
                    CatalogResponse catalogResponse = contentById.get(projection.getContentId());

                    return new GenreTrendResponse(
                            projection.getContentId(),
                            catalogResponse.genre(),
                            catalogResponse.title(),
                            projection.getTotalWatchTimeMs()
                    );
                })
                .toList();

        metricsCacheService.put(key, response, cacheTTLStrategy.trendingTtl());
        return response;
    }
}
