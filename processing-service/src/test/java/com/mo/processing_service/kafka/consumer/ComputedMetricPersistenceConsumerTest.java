package com.mo.processing_service.kafka.consumer;

import com.mo.common.kafka.enums.EventType;
import com.mo.common.kafka.enums.MetricType;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.common.kafka.events.ComputedMetricEvent;
import com.mo.processing_service.entity.CompletionMetric;
import com.mo.processing_service.entity.ConcurrentViewerSnapshot;
import com.mo.processing_service.entity.DropoffHeatmap;
import com.mo.processing_service.entity.WatchTimeMetric;
import com.mo.processing_service.service.CompletionRateService;
import com.mo.processing_service.service.ConcurrentViewerService;
import com.mo.processing_service.service.DropoffHeatmapService;
import com.mo.processing_service.service.WatchTimeMetricService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ComputedMetricPersistenceConsumerTest {

    @Mock
    private WatchTimeMetricService watchTimeMetricService;

    @Mock
    private CompletionRateService completionRateService;

    @Mock
    private DropoffHeatmapService dropoffHeatmapService;

    @Mock
    private ConcurrentViewerService concurrentViewerService;

    @InjectMocks
    private ComputedMetricPersistenceConsumer consumer;

    @Test
    void consume_dispatchesWatchTimeMetric() {
        WatchTimeMetric metric = new WatchTimeMetric(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                1000,
                500,
                2,
                2,
                OffsetDateTime.now()
        );
        EventEnvelope<ComputedMetricEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID(),
                EventType.COMPUTED_METRICS_EVENTS,
                OffsetDateTime.now(),
                new ComputedMetricEvent(UUID.randomUUID(), MetricType.WATCH_TIME, OffsetDateTime.now(), OffsetDateTime.now(), metric, OffsetDateTime.now())
        );

        consumer.consume(envelope);

        verify(watchTimeMetricService).process(metric);
    }

    @Test
    void consume_dispatchesCompletionRateMetric() {
        CompletionMetric metric = new CompletionMetric(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                7,
                4,
                0.57,
                OffsetDateTime.now()
        );
        EventEnvelope<ComputedMetricEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID(),
                EventType.COMPUTED_METRICS_EVENTS,
                OffsetDateTime.now(),
                new ComputedMetricEvent(UUID.randomUUID(), MetricType.COMPLETION_RATE, OffsetDateTime.now(), OffsetDateTime.now(), metric, OffsetDateTime.now())
        );

        consumer.consume(envelope);

        verify(completionRateService).process(metric);
    }

    @Test
    void consume_dispatchesDropoffMetric() {
        DropoffHeatmap metric = new DropoffHeatmap(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                (short) 20,
                10,
                OffsetDateTime.now()
        );
        EventEnvelope<ComputedMetricEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID(),
                EventType.COMPUTED_METRICS_EVENTS,
                OffsetDateTime.now(),
                new ComputedMetricEvent(UUID.randomUUID(), MetricType.DROPOFF_HEATMAP, OffsetDateTime.now(), OffsetDateTime.now(), metric, OffsetDateTime.now())
        );

        consumer.consume(envelope);

        verify(dropoffHeatmapService).process(metric);
    }

    @Test
    void consume_dispatchesConcurrentViewerMetric() {
        ConcurrentViewerSnapshot metric = new ConcurrentViewerSnapshot(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                25,
                18.5,
                OffsetDateTime.now()
        );
        EventEnvelope<ComputedMetricEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID(),
                EventType.COMPUTED_METRICS_EVENTS,
                OffsetDateTime.now(),
                new ComputedMetricEvent(UUID.randomUUID(), MetricType.CONCURRENT_VIEWERS, OffsetDateTime.now(), OffsetDateTime.now(), metric, OffsetDateTime.now())
        );

        consumer.consume(envelope);

        verify(concurrentViewerService).process(metric);
    }
}
