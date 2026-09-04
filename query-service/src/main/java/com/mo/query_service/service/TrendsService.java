package com.mo.query_service.service;

import com.mo.query_service.client.CatalogClient;
import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.dto.response.CatalogResponse;
import com.mo.query_service.dto.response.GenreTrendResponse;
import com.mo.query_service.dto.response.MostCompletedResponse;
import com.mo.query_service.dto.response.TopContentResponse;
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

    private final CatalogClient catalogClient;

    public List<TopContentResponse> getTopContent(MetricsQueryRequest queryParams) {
        List<TopContentProjection> projections =
                watchTimeMetricsRepository.getTopContent(
                        queryParams.from(),
                        queryParams.to(),
                        queryParams.size(),
                        queryParams.offset()
                );

        return projections.stream()
                .map(
                        projection -> new TopContentResponse(
                                projection.getContentId(),
                                projection.getTotalWatchTimeMs()
                        )
                )
                .toList();
    }

    public List<MostCompletedResponse> getMostCompleted(MetricsQueryRequest queryParams) {
        List<MostCompletedProjection> projections =
                completionMetricsRepository.findMostCompleted(
                        queryParams.from(),
                        queryParams.to(),
                        queryParams.size(),
                        queryParams.offset()
                );

        return projections.stream()
                .map(projection -> new MostCompletedResponse(
                        projection.getContentId(),
                        projection.getCompleteCount()
                ))
                .toList();
    }

    public List<GenreTrendResponse> getGenreTrend(String genre, MetricsQueryRequest queryParams) {
        List<CatalogResponse> contents = catalogClient.getContentByGenre(genre);
        if(contents == null) { return List.of(); }

        Map<UUID, CatalogResponse> contentById = contents.stream()
                .collect(Collectors.toMap(CatalogResponse::contentId, Function.identity()));

        List<UUID> contentIds = contents.stream().map(CatalogResponse::contentId).toList();

        List<GenreTrendProjection> projections =
                watchTimeMetricsRepository.findGenreTrend(
                        contentIds,
                        queryParams.from(),
                        queryParams.to()
                );

        return projections.stream()
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
    }
}
