package com.mo.query_service.service;

import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.dto.response.ContentComparisonResponse;
import com.mo.query_service.dto.response.PlatformOverviewResponse;
import com.mo.query_service.projections.ContentComparisonProjection;
import com.mo.query_service.projections.PlatformOverviewProjection;
import com.mo.query_service.repository.AdminMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminMetricsService {
    private final AdminMetricsRepository adminMetricsRepository;

    public PlatformOverviewResponse getPlatformOverview(MetricsQueryRequest queryParams) {
        PlatformOverviewProjection projection =
                adminMetricsRepository.getPlatformOverView(queryParams.from(), queryParams.to());

        BigDecimal completionRate =
                projection.getPlayCount() == 0
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(projection.getCompleteCount())
                                .divide(
                                        BigDecimal.valueOf(projection.getPlayCount()),
                                        4,
                                        RoundingMode.HALF_UP
                                )
                                .multiply(BigDecimal.valueOf(100));


        return new PlatformOverviewResponse(
                projection.getTotalWatchTimeMs(),
                projection.getAvgWatchDurationMs(),
                projection.getUniqueSessions(),
                projection.getUniqueUsers(),
                projection.getPlayCount(),
                projection.getCompleteCount(),
                completionRate,
                projection.getPeakConcurrentViewers(),
                projection.getAvgConcurrentViewers()
        );
    }

    public List<ContentComparisonResponse> contentComparison(
            List<UUID> contentIds,
            MetricsQueryRequest queryParams
    ) {
        if (contentIds == null || contentIds.isEmpty()) {
            return List.of();
        }

        List<ContentComparisonProjection> projections =
                adminMetricsRepository.contentComparison(
                        contentIds,
                        queryParams.from(),
                        queryParams.to()
                );

        return projections.stream()
                .map(projection -> {
                    BigDecimal completionRate =
                            projection.getPlayCount() == 0
                                    ? BigDecimal.ZERO
                                    : BigDecimal.valueOf(projection.getCompleteCount())
                                    .divide(
                                            BigDecimal.valueOf(projection.getPlayCount()),
                                            4,
                                            RoundingMode.HALF_UP
                                    )
                                    .multiply(BigDecimal.valueOf(100));
                    return new ContentComparisonResponse(
                            projection.getContentId(),
                            projection.getTotalWatchTimeMs(),
                            projection.getAvgWatchDurationMs(),
                            projection.getPlayCount(),
                            projection.getCompleteCount(),
                            completionRate,
                            projection.getPeekConcurrentViewers(),
                            projection.getAvgConcurrentViewers()
                    );
                })
                .toList();
    }
}
