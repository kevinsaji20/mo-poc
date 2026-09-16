package com.mo.query_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mo.query_service.cache.CacheTTLStrategy;
import com.mo.query_service.cache.MetricsCacheService;
import com.mo.query_service.client.CatalogClient;
import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.dto.response.*;
import com.mo.query_service.enums.Granularity;
import com.mo.query_service.exception.ContentNotFoundException;
import com.mo.query_service.projections.*;
import com.mo.query_service.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {

    @Mock private CatalogClient catalogClient;
    @Mock private WatchTimeMetricsRepository watchTimeMetricsRepository;
    @Mock private CompletionMetricsRepository completionMetricsRepository;
    @Mock private DropoffHeatmapRepository dropoffHeatmapRepository;
    @Mock private ConcurrentViewersSnapshotRepository concurrentViewersSnapshotRepository;
    @Mock private MetricsSummaryRepository metricsSummaryRepository;
    @Mock private MetricsCacheService metricsCacheService;
    @Mock private CacheTTLStrategy cacheTTLStrategy;

    @InjectMocks private MetricsService metricsService;

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
    void getSummary_whenContentExistsAndCacheMiss_fetchesAndCaches() {
        UUID contentId = UUID.randomUUID();
        SummaryResponse expected = new SummaryResponse(100L, 50L, 10L, 7L, 20L, 11L, new BigDecimal("55.00"), 30, new BigDecimal("20.00"));
        when(catalogClient.contentExists(contentId)).thenReturn(true);
        when(metricsCacheService.get(anyString(), eq(SummaryResponse.class))).thenReturn(null);
        when(metricsSummaryRepository.getSummary(contentId, query.from(), query.to())).thenReturn(expected);
        when(cacheTTLStrategy.contentMetricsTtl(query.to())).thenReturn(Duration.ofMinutes(5));

        SummaryResponse actual = metricsService.getSummary(contentId, query);

        assertThat(actual).isEqualTo(expected);
        verify(metricsSummaryRepository).getSummary(contentId, query.from(), query.to());
        verify(metricsCacheService).put(anyString(), eq(expected), eq(Duration.ofMinutes(5)));
    }

    @Test
    void getSummary_whenContentMissing_throwsContentNotFoundException() {
        UUID contentId = UUID.randomUUID();
        when(catalogClient.contentExists(contentId)).thenReturn(false);

        assertThatThrownBy(() -> metricsService.getSummary(contentId, query))
                .isInstanceOf(ContentNotFoundException.class);
    }

    @Test
    void getWatchTime_whenCacheHit_returnsCachedResults() {
        UUID contentId = UUID.randomUUID();
        OffsetDateTime bucket = OffsetDateTime.parse("2024-01-01T00:00:00Z");
        List<WatchTimeResponse> cached = List.of(new WatchTimeResponse(bucket, 100L, 2L, 1L));
        when(catalogClient.contentExists(contentId)).thenReturn(true);
        when(metricsCacheService.get(anyString(), any(TypeReference.class))).thenReturn(cached);

        List<WatchTimeResponse> actual = metricsService.getWatchTime(contentId, query);

        assertThat(actual).isEqualTo(cached);
        verify(watchTimeMetricsRepository, never()).findWatchTime(any(), any(), any(), any());
    }

    @Test
    void getCompletion_calculatesRateAndCaches() {
        UUID contentId = UUID.randomUUID();
        CompletionProjection projection = mock(CompletionProjection.class);
        when(projection.getBucket()).thenReturn(OffsetDateTime.parse("2024-01-01T00:00:00Z"));
        when(projection.getPlayCount()).thenReturn(100L);
        when(projection.getCompleteCount()).thenReturn(40L);
        when(catalogClient.contentExists(contentId)).thenReturn(true);
        when(metricsCacheService.get(anyString(), any(TypeReference.class))).thenReturn(null);
        when(completionMetricsRepository.findCompletion(contentId, query.from(), query.to(), query.granularity().name()))
                .thenReturn(List.of(projection));
        when(cacheTTLStrategy.contentMetricsTtl(query.to())).thenReturn(Duration.ofMinutes(10));

        List<CompletionResponse> actual = metricsService.getCompletion(contentId, query);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).completionRate()).isEqualByComparingTo(new BigDecimal("40.0000"));
        verify(metricsCacheService).put(anyString(), anyList(), eq(Duration.ofMinutes(10)));
    }

    @Test
    void getDropoff_whenCacheMiss_mapsProjections() {
        UUID contentId = UUID.randomUUID();
        DropoffProjection projection = mock(DropoffProjection.class);
        when(projection.getPostionBucket()).thenReturn((short) 30);
        when(projection.getStopCount()).thenReturn(12L);
        when(catalogClient.contentExists(contentId)).thenReturn(true);
        when(metricsCacheService.get(anyString(), any(TypeReference.class))).thenReturn(null);
        when(dropoffHeatmapRepository.findDropoff(contentId, query.from(), query.to())).thenReturn(List.of(projection));
        when(cacheTTLStrategy.contentMetricsTtl(query.to())).thenReturn(Duration.ofMinutes(2));

        List<DropoffResponse> actual = metricsService.getDropoff(contentId, query);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).positionBucket()).isEqualTo((short) 30);
        assertThat(actual.get(0).stopCount()).isEqualTo(12L);
    }

    @Test
    void getConcurrentViewers_mapsProjectionValues() {
        UUID contentId = UUID.randomUUID();
        ConcurrentViewersProjection projection = mock(ConcurrentViewersProjection.class);
        when(projection.getBucket()).thenReturn(OffsetDateTime.parse("2024-01-01T00:00:00Z"));
        when(projection.getPeakViewers()).thenReturn(8);
        when(projection.getAvgViewers()).thenReturn(new BigDecimal("3.25"));
        when(catalogClient.contentExists(contentId)).thenReturn(true);
        when(metricsCacheService.get(anyString(), any(TypeReference.class))).thenReturn(null);
        when(concurrentViewersSnapshotRepository.findConcurrentViewersTrend(contentId, query.from(), query.to(), query.granularity().name()))
                .thenReturn(List.of(projection));
        when(cacheTTLStrategy.contentMetricsTtl(query.to())).thenReturn(Duration.ofMinutes(7));

        List<ConcurrentViewersResponse> actual = metricsService.getConcurrentViewers(contentId, query);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).peakViewers()).isEqualTo(8);
        assertThat(actual.get(0).avgViewers()).isEqualByComparingTo(new BigDecimal("3.25"));
    }
}
