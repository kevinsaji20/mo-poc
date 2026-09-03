package com.mo.processing_service.kafka.topology;

import com.mo.common.kafka.enums.IngestionType;
import com.mo.common.kafka.enums.MetricType;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.common.kafka.events.ComputedMetricEvent;
import com.mo.common.kafka.events.IngestionEvent;
import com.mo.processing_service.entity.ConcurrentViewerSnapshot;
import com.mo.processing_service.entity.DropoffHeatmap;
import com.mo.processing_service.kafka.producer.ComputedMetricsProducer;
import com.mo.processing_service.kafka.state.concurrent.ConcurrentViewerAggregate;
import com.mo.processing_service.kafka.state.concurrent.ConcurrentViewerState;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConcurrentViewerTopology {
    private static final Duration CONCURRENT_VIEWER_WINDOW = Duration.ofMinutes(5);

    private final JsonSerde<ConcurrentViewerState> concurrentViewerStateSerde;
    private final JsonSerde<ConcurrentViewerAggregate> concurrentViewerAggregateSerde;

    private final ComputedMetricsProducer computedMetricsProducer;

    public void build (
            KStream<String, EventEnvelope<IngestionEvent>> events
    ) {
        KTable<String, ConcurrentViewerState> activeViewerState =
                events
                        .filter(
                                (key, envelope) ->
                                        envelope != null &&
                                                envelope.payload() != null &&
                                                isRelevantEvent(envelope.payload().eventType())
                        )
                        .selectKey(
                                (key, envelope) ->
                                        envelope.payload().contentId().toString()
                        )
                        .groupByKey()
                        .aggregate(
                                ConcurrentViewerState::new,
                                (contentId, envelope, state) -> {
                                    IngestionEvent event = envelope.payload();

                                    switch (event.eventType()) {
                                        case PLAY -> state.activate(event.sessionId(), event.userId());
                                        case STOP, COMPLETE -> state.deactivate(event.sessionId());
                                    }

                                    return state;
                                },
                                Materialized
                                        .<String, ConcurrentViewerState, KeyValueStore<Bytes, byte[]>>as(
                                                "concurrent-viewer-state"
                                        )
                                        .withKeySerde(Serdes.String())
                                        .withValueSerde(concurrentViewerStateSerde)
                        );
        KTable<Windowed<String>, ConcurrentViewerAggregate> aggregates =
                activeViewerState
                        .toStream()
                        .mapValues(ConcurrentViewerState::getDistinctViewerCount)
                        .groupByKey(
                                Grouped.with(
                                        Serdes.String(),
                                        Serdes.Integer()
                                )
                        )
                        .windowedBy(
                                TimeWindows.ofSizeWithNoGrace(CONCURRENT_VIEWER_WINDOW)
                        )
                        .aggregate(
                                ConcurrentViewerAggregate::new,
                                (contentId, viewerCount, aggregate) -> {
                                    aggregate.record(viewerCount);
                                    return aggregate;
                                },
                                Materialized
                                        .<String, ConcurrentViewerAggregate, WindowStore<Bytes, byte[]>>as(
                                                "concurrent-viewer-aggregate"
                                        )
                                        .withKeySerde(Serdes.String())
                                        .withValueSerde(concurrentViewerAggregateSerde)
                        );
        aggregates
                .suppress(
                        Suppressed.untilWindowCloses(
                                Suppressed.BufferConfig.unbounded()
                        )
                )
                .toStream()
                .map(this::createMetric)
                .foreach(
                        (windowedKey, metric) ->
                                computedMetricsProducer.publishComputedMetricsEvents(
                                        toComputedMetricEvent(metric)
                                )
                );
    }

    private KeyValue<Windowed<String>, ConcurrentViewerSnapshot> createMetric (
            Windowed<String> windowedKey,
            ConcurrentViewerAggregate aggregate
    ) {
        UUID contentId = UUID.fromString(windowedKey.key());

        OffsetDateTime windowStart =
                OffsetDateTime.ofInstant(
                        windowedKey.window().startTime(),
                        ZoneOffset.UTC
                );

        OffsetDateTime windowEnd =
                OffsetDateTime.ofInstant(
                        windowedKey.window().endTime(),
                        ZoneOffset.UTC
                );

        ConcurrentViewerSnapshot metric = new ConcurrentViewerSnapshot(
                contentId,
                windowStart,
                windowEnd,
                aggregate.getPeakViewers(),
                aggregate.getAverageViewers(),
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        return KeyValue.pair(windowedKey, metric);
    }

    private ComputedMetricEvent toComputedMetricEvent(
            ConcurrentViewerSnapshot metric
    ) {
        return new ComputedMetricEvent(
                metric.contentId(),
                MetricType.CONCURRENT_VIEWERS,
                metric.windowStart(),
                metric.windowEnd(),
                metric,
                metric.computedAt()
        );
    }

    private boolean isRelevantEvent(IngestionType eventType) {
        return eventType == IngestionType.PLAY
                || eventType == IngestionType.PAUSE
                || eventType == IngestionType.SEEK
                || eventType == IngestionType.STOP
                || eventType == IngestionType.COMPLETE;
    }
}
