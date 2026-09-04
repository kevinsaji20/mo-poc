package com.mo.query_service.service;

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


    public SummaryResponse getSummary(UUID contentId, MetricsQueryRequest request) {
        if (!catalogClient.contentExists(contentId)) {
            throw new ContentNotFoundException();
        }

        MetricsSummaryProjection projection =
                metricsSummaryRepository.getSummary(
                        contentId,
                        request.from(),
                        request.to()
                );

        BigDecimal completionRate =
                projection.getPlayCount() == 0
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(projection.getCompleteCount())
                          .divide(BigDecimal.valueOf(projection.getPlayCount()), 4, RoundingMode.HALF_UP)
                          .multiply(BigDecimal.valueOf(100));

        return new SummaryResponse(
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
    }

    public List<WatchTimeResponse> getWatchTime(UUID contentId, MetricsQueryRequest request) {
        if(!catalogClient.contentExists(contentId)) {
            throw new ContentNotFoundException();
        }

        String granularity = request.granularity().toString().toLowerCase();

        List<WatchTimeProjection> projections =
                watchTimeMetricsRepository
                        .findWatchTime(
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

        List<CompletionProjection> projections =
                completionMetricsRepository.findCompletion(
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

    public List<DropoffResponse> getDropoff(UUID contentId, MetricsQueryRequest request) {
        if(!catalogClient.contentExists(contentId)) {
            throw new ContentNotFoundException();
        }

        String granularity = request.granularity().toString().toLowerCase();

        List<DropoffProjection> projections =
                dropoffHeatmapRepository.findDropoff(
                        contentId,
                        request.from(),
                        request.to()
                );
        return projections.stream()
                .map(projection -> new DropoffResponse(
                        projection.getPostionBucket(),
                        projection.getStopCount()
                ))
                .toList();
    }

    public List<ConcurrentViewersResponse> getConcurrentViewers(
            UUID contentId,
            MetricsQueryRequest query
    ) {
        if (!catalogClient.contentExists(contentId)) {
            throw new ContentNotFoundException();
        }

        List<ConcurrentViewersProjection> projections =
                concurrentViewersSnapshotRepository.findConcurrentViewersTrend(
                        contentId,
                        query.from(),
                        query.to(),
                        query.granularity().name()
                );

        return projections.stream()
                .map(projection -> new ConcurrentViewersResponse(
                        projection.getBucket(),
                        projection.getPeakViewers(),
                        projection.getAvgViewers()
                ))
                .toList();
    }
}
