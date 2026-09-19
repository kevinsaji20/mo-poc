package com.mo.processing_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.common.kafka.constants.KafkaConsumers;
import com.mo.common.kafka.constants.KafkaTopics;
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
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComputedMetricPersistenceConsumer {
    private final ObjectMapper objectMapper;

    private final WatchTimeMetricService watchTimeMetricService;
    private final CompletionRateService completionRateService;
    private final DropoffHeatmapService dropoffHeatmapService;
    private final ConcurrentViewerService concurrentViewerService;

    private <T> T convertValue(Object value, Class<T> type) {
        return objectMapper.convertValue(value, type);
    }

    @KafkaListener(
            topics = KafkaTopics.COMPUTED_METRICS_EVENTS,
            groupId = KafkaConsumers.METRIC_PERSISTENCE
    )
    public void consume(EventEnvelope<ComputedMetricEvent> envelope) {
        ComputedMetricEvent event = envelope.payload();
         switch (event.metricType()) {
            case WATCH_TIME -> watchTimeMetricService.process(
                    convertValue(event.value(), WatchTimeMetric.class)
            );
            case COMPLETION_RATE -> completionRateService.process(
                    convertValue(event.value(), CompletionMetric.class)
            );
            case DROPOFF_HEATMAP -> dropoffHeatmapService.process(
                    convertValue(event.value(), DropoffHeatmap.class)
            );
            case CONCURRENT_VIEWERS -> concurrentViewerService.process(
                    convertValue(event.value(), ConcurrentViewerSnapshot.class)
            );
        }
    }
}
