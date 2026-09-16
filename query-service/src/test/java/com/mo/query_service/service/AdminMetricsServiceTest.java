package com.mo.query_service.service;

import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.dto.response.ContentComparisonResponse;
import com.mo.query_service.dto.response.PlatformOverviewResponse;
import com.mo.query_service.enums.Granularity;
import com.mo.query_service.repository.AdminMetricsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminMetricsServiceTest {

    @Mock private AdminMetricsRepository adminMetricsRepository;

    @InjectMocks private AdminMetricsService adminMetricsService;

    private final MetricsQueryRequest query = new MetricsQueryRequest(
            OffsetDateTime.parse("2024-01-01T00:00:00Z"),
            OffsetDateTime.parse("2024-01-02T00:00:00Z"),
            Granularity.HOUR,
            1,
            20,
            0,
            null,
            null
    );

    @Test
    void getPlatformOverview_delegatesToRepository() {
        PlatformOverviewResponse expected = new PlatformOverviewResponse(100L, 50L, 10L, 7L, 20L, 11L, new BigDecimal("55.00"), 30, new BigDecimal("20.00"));
        when(adminMetricsRepository.getPlatformOverview(query.from(), query.to())).thenReturn(expected);

        PlatformOverviewResponse actual = adminMetricsService.getPlatformOverview(query);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void contentComparison_returnsEmptyListWhenContentIdsAreMissing() {
        assertThat(adminMetricsService.contentComparison(List.of(), query)).isEmpty();
        assertThat(adminMetricsService.contentComparison(null, query)).isEmpty();
    }

    @Test
    void contentComparison_delegatesToRepository() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        ContentComparisonResponse response = new ContentComparisonResponse(id1, 100L, 50L, 20L, 10L, new BigDecimal("50.00"), 30, new BigDecimal("15.00"));
        when(adminMetricsRepository.contentComparison(List.of(id1, id2), query.from(), query.to())).thenReturn(List.of(response));

        List<ContentComparisonResponse> actual = adminMetricsService.contentComparison(List.of(id1, id2), query);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(response);
        verify(adminMetricsRepository).contentComparison(List.of(id1, id2), query.from(), query.to());
    }
}
