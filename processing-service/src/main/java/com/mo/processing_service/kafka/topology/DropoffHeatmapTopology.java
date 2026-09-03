package com.mo.processing_service.kafka.topology;

import com.mo.common.kafka.enums.IngestionType;
import com.mo.common.kafka.enums.MetricType;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.common.kafka.events.ComputedMetricEvent;
import com.mo.common.kafka.events.IngestionEvent;
import com.mo.processing_service.entity.CompletionMetric;
import com.mo.processing_service.entity.DropoffHeatmap;
import com.mo.processing_service.kafka.producer.ComputedMetricsProducer;
import com.mo.processing_service.kafka.state.dropoff.DropoffAggregate;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DropoffHeatmapTopology {
    private static final Duration DROPOFF_WINDOW = Duration.ofHours(1);

    private final JsonSerde<DropoffAggregate> dropoffAggregateSerde;

    private final ComputedMetricsProducer computedMetricsProducer;

    public void build(
            KStream<String, EventEnvelope<IngestionEvent>> events
    ) {
        KTable<Windowed<String>, DropoffAggregate> dropoffMetrics =
                events
                        .filter(
                                (key, envelope) ->
                                        envelope != null &&
                                                envelope.payload() != null &&
                                                isDropoffEvent(
                                                        envelope.payload().eventType()
                                                )
                        )
                        .selectKey(
                                (key, envelope) -> {
                                    IngestionEvent event = envelope.payload();
                                    int bucket =
                                            calculatePositionBucket(
                                                    event.playbackPositionMs(),
                                                    event.totalDurationMs()
                                            );

                                    return event.contentId() + ":" + bucket;
                                }
                        )
                        .groupByKey()
                        .windowedBy(
                                TimeWindows.ofSizeWithNoGrace(DROPOFF_WINDOW)
                        )
                        .aggregate(
                                DropoffAggregate::new,
                                (key, event, aggregate) -> {
                                    aggregate.addStop();
                                    return aggregate;
                                },
                                Materialized
                                        .<String, DropoffAggregate, WindowStore<Bytes, byte[]>>as(
                                                "dropoff-heatmap-store"
                                        )
                                        .withKeySerde(Serdes.String())
                                        .withValueSerde(dropoffAggregateSerde)
                        );
        dropoffMetrics
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

    private KeyValue<Windowed<String>, DropoffHeatmap> createMetric(
            Windowed<String> windowedKey,
            DropoffAggregate aggregate
    ) {
        String[] parts = windowedKey.key().split(":");
        UUID contentId = UUID.fromString(parts[0]);
        int positionBucket = Integer.parseInt(parts[1]);

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

        DropoffHeatmap metric = new DropoffHeatmap(
                contentId,
                windowStart,
                windowEnd,
                (short) positionBucket,
                aggregate.getStopCount(),
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        return KeyValue.pair(windowedKey, metric);
    }

    private ComputedMetricEvent toComputedMetricEvent(
            DropoffHeatmap metric
    ) {
        return new ComputedMetricEvent(
                metric.contentId(),
                MetricType.COMPLETION_RATE,
                metric.windowStart(),
                metric.windowEnd(),
                metric,
                metric.computedAt()
        );
    }

    private int calculatePositionBucket(
            long playbackPositionMs,
            long totalDurationMs
    ) {
        if (totalDurationMs <= 0) {
            return 0;
        }
        double percentage = (double) (playbackPositionMs/totalDurationMs) * 100;
        return Math.min(90, (int) (percentage / 10) * 10);
    }

    private boolean isDropoffEvent(IngestionType eventType) {
        return eventType == IngestionType.STOP;
    }
}
