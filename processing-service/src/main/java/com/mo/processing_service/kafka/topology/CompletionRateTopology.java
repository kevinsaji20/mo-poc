package com.mo.processing_service.kafka.topology;

import com.mo.common.kafka.enums.IngestionType;
import com.mo.common.kafka.enums.MetricType;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.common.kafka.events.ComputedMetricEvent;
import com.mo.common.kafka.events.IngestionEvent;
import com.mo.processing_service.entity.CompletionMetric;
import com.mo.processing_service.kafka.producer.ComputedMetricsProducer;
import com.mo.processing_service.kafka.state.completion.CompletionAggregate;
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
public class CompletionRateTopology {
    public static final Duration COMPLETION_WINDOW = Duration.ofHours(1);

    private final JsonSerde<CompletionAggregate> completionAggregateSerde;

    private final ComputedMetricsProducer computedMetricsProducer;

    public void build (
            KStream<String, EventEnvelope<IngestionEvent>> events
    ) {
        KTable<Windowed<UUID>, CompletionAggregate> completionMetrics =
                events
                        .filter(
                                (key, envelope) ->
                                        envelope != null &&
                                                envelope.payload() != null &&
                                                isCompletionEvent(
                                                        envelope.payload().eventType()
                                                )
                        )
                        .selectKey(
                                (key, envelope) ->
                                        envelope.payload().contentId()
                        )
                        .groupByKey()
                        .windowedBy(
                                TimeWindows.ofSizeWithNoGrace(COMPLETION_WINDOW)
                        )
                        .aggregate(
                                CompletionAggregate::new,
                                (contentId, envelope, aggregate) -> {
                                    IngestionEvent event = envelope.payload();
                                    if (event.eventType() == IngestionType.PLAY) {
                                        aggregate.addPlay();
                                    }
                                    if (event.eventType() == IngestionType.COMPLETE) {
                                        aggregate.addComplete();
                                    }

                                    return aggregate;
                                },
                                Materialized
                                        .<UUID, CompletionAggregate, WindowStore<Bytes, byte[]>>as(
                                                "completion-rate-store"
                                        )
                                        .withKeySerde(Serdes.UUID())
                                        .withValueSerde(completionAggregateSerde)
                        );
        completionMetrics
                .suppress(
                        Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded())
                )
                .toStream()
                .map(this::createMetric)
                .foreach(
                        (windowedKey, metric) -> {
                            computedMetricsProducer.publishComputedMetricsEvents(
                                    toComputedMetricEvent(metric)
                            );
                        }
                );
    }

    private KeyValue<Windowed<UUID>, CompletionMetric> createMetric(
            Windowed<UUID> windowedKey,
            CompletionAggregate aggregate
    ) {
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
        CompletionMetric metric = new CompletionMetric(
                windowedKey.key(),
                windowStart,
                windowEnd,
                aggregate.getPlayCount(),
                aggregate.getCompletionCount(),
                aggregate.getCompletionRate(),
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        return KeyValue.pair(windowedKey, metric);
    }

    private ComputedMetricEvent toComputedMetricEvent(
            CompletionMetric metric
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

    private boolean isCompletionEvent(IngestionType eventType) {
        return eventType == IngestionType.PLAY
                || eventType == IngestionType.COMPLETE;
    }
}
