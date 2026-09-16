package com.mo.query_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mo.query_service.cache.CacheTTLStrategy;
import com.mo.query_service.cache.MetricsCacheService;
import com.mo.query_service.client.CatalogClient;
import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.dto.response.*;
import com.mo.query_service.enums.Granularity;
import com.mo.query_service.projections.*;
import com.mo.query_service.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrendsServiceTest {

    @Mock private WatchTimeMetricsRepository watchTimeMetricsRepository;
    @Mock private CompletionMetricsRepository completionMetricsRepository;
    @Mock private MetricsCacheService metricsCacheService;
    @Mock private CacheTTLStrategy cacheTTLStrategy;
    @Mock private CatalogClient catalogClient;

    @InjectMocks private TrendsService trendsService;

    private final MetricsQueryRequest query = new MetricsQueryRequest(
            OffsetDateTime.parse("2024-01-01T00:00:00Z"),
            OffsetDateTime.parse("2024-01-02T00:00:00Z"),
            Granularity.DAY,
            1,
            10,
            0,
            null,
            null
    );

    @Test
    void getTopContent_whenCacheMiss_fetchesAndCaches() {
        TopContentProjection projection = mock(TopContentProjection.class);
        when(projection.getContentId()).thenReturn(UUID.randomUUID());
        when(projection.getTotalWatchTimeMs()).thenReturn(1200L);
        when(metricsCacheService.get(anyString(), any(TypeReference.class))).thenReturn(null);
        when(watchTimeMetricsRepository.getTopContent(query.from(), query.to(), query.size(), query.offset())).thenReturn(List.of(projection));
        when(cacheTTLStrategy.trendingTtl()).thenReturn(Duration.ofMinutes(15));

        List<TopContentResponse> actual = trendsService.getTopContent(query);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).totalWatchTimeMs()).isEqualTo(1200L);
        verify(metricsCacheService).put(anyString(), anyList(), eq(Duration.ofMinutes(15)));
    }

    @Test
    void getMostCompleted_usesRepositoryAndCache() {
        MostCompletedProjection projection = mock(MostCompletedProjection.class);
        UUID contentId = UUID.randomUUID();
        when(projection.getContentId()).thenReturn(contentId);
        when(projection.getCompleteCount()).thenReturn(50L);
        when(metricsCacheService.get(anyString(), any(TypeReference.class))).thenReturn(null);
        when(completionMetricsRepository.findMostCompleted(query.from(), query.to(), query.size(), query.offset())).thenReturn(List.of(projection));
        when(cacheTTLStrategy.trendingTtl()).thenReturn(Duration.ofMinutes(25));

        List<MostCompletedResponse> actual = trendsService.getMostCompleted(query);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).contentId()).isEqualTo(contentId);
        assertThat(actual.get(0).completeCount()).isEqualTo(50L);
    }

    @Test
    void getGenreTrend_whenCatalogResultsAreEmpty_returnsEmptyList() {
        when(metricsCacheService.get(anyString(), any(TypeReference.class))).thenReturn(null);
        when(catalogClient.getContentByGenre("Drama")).thenReturn(List.of());
        when(cacheTTLStrategy.trendingTtl()).thenReturn(Duration.ofMinutes(10));

        List<GenreTrendResponse> actual = trendsService.getGenreTrend("Drama", query);

        assertThat(actual).isEmpty();
        verify(metricsCacheService).put(anyString(), eq(List.of()), eq(Duration.ofMinutes(10)));
    }
}
