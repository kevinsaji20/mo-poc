package com.mo.query_service.service;

import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.dto.response.ContentComparisonResponse;
import com.mo.query_service.dto.response.PlatformOverviewResponse;
import com.mo.query_service.repository.AdminMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminMetricsService {
    private final AdminMetricsRepository adminMetricsRepository;

    public PlatformOverviewResponse getPlatformOverview(MetricsQueryRequest queryParams) {
        return adminMetricsRepository.getPlatformOverview(queryParams.from(), queryParams.to());
    }

    public List<ContentComparisonResponse> contentComparison(
            List<UUID> contentIds,
            MetricsQueryRequest queryParams
    ) {
        if (contentIds == null || contentIds.isEmpty()) {
            return List.of();
        }

        return adminMetricsRepository.contentComparison(
                        contentIds,
                        queryParams.from(),
                        queryParams.to()
                );
    }
}
