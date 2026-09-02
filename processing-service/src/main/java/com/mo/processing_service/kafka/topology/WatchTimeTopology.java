package com.mo.processing_service.kafka.topology;

import com.mo.common.kafka.enums.IngestionType;
import com.mo.common.kafka.enums.MetricType;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.common.kafka.events.ComputedMetricEvent;
import com.mo.common.kafka.events.IngestionEvent;
import com.mo.processing_service.entity.WatchTimeMetric;
import com.mo.processing_service.kafka.producer.ComputedMetricsProducer;
import com.mo.processing_service.kafka.state.WatchTimeAggregate;
import com.mo.processing_service.kafka.state.WatchTimeContribution;
import com.mo.processing_service.kafka.state.WatchTimeSessionState;
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
public class WatchTimeTopology {
    private static final Duration WATCH_TIME_WINDOW = Duration.ofMinutes(5);

    private final JsonSerde<WatchTimeSessionState> sessionStateSerde;
    private final JsonSerde<WatchTimeAggregate> aggregateSerde;
    private final JsonSerde<ComputedMetricEvent> computedMetricEventSerde;

    private final ComputedMetricsProducer computedMetricsProducer;

    public void build(
            KStream<String, EventEnvelope<IngestionEvent>> events
    ) {
        KTable<String, WatchTimeSessionState> sessionStates =
                events
                        .filter (
                                (key, envelope) ->
                                        envelope != null &&
                                        envelope.payload() != null &&
                                        isWatchTimeEvent(
                                            envelope.payload().eventType()
                                        )
                        )
                        .selectKey(
                                (key, envelope) ->
                                        envelope.payload().sessionId().toString()
                        )
                        .groupByKey()
                        .aggregate(
                                WatchTimeSessionState::new,
                                (sessionId, envelope, state) -> {
                                    IngestionEvent event = envelope.payload();
                                    if (state.getSessionId() == null) {
                                        state.setSessionId(event.sessionId());
                                        state.setContentId(event.contentId());
                                        state.setUserId(event.userId());
                                    }
                                    state.process(
                                            event.eventType(),
                                            event.playbackPositionMs(),
                                            event.seekFromPosition()
                                    );
                                    return state;
                                },
                                Materialized
                                        .<String,WatchTimeSessionState, KeyValueStore<Bytes, byte[]>>as(
                                                "watch-time-store"
                                        )
                                        .withKeySerde(Serdes.String())
                                        .withValueSerde(sessionStateSerde)
                        );

        KTable<Windowed<UUID>, WatchTimeAggregate> watchTimeMetrics =
                sessionStates.toStream()
                        .filter(
                                (sessionId, state) ->
                                        state != null &&
                                                state.getContentId() != null &&
                                                state.getAccumulatedWatchTimeMs() > 0
                        )
                        .mapValues(
                                state ->
                                        new WatchTimeContribution(
                                                state.getSessionId(),
                                                state.getContentId(),
                                                state.getUserId(),
                                                state.getWatchTimeDeltaMs()
                                        )
                        )
                        .selectKey(
                                (contentId, contribution) ->
                                        contribution.getContentId()
                        )
                        .groupByKey(
                                Grouped.with(
                                        Serdes.UUID(),
                                        new JsonSerde<>(WatchTimeContribution.class)
                                )
                        )
                        .windowedBy(
                                TimeWindows.ofSizeWithNoGrace(WATCH_TIME_WINDOW)
                        )
                        .aggregate(
                                WatchTimeAggregate::new,
                                (contentId, contribution, aggregate) -> {
                                    aggregate.add(contribution);
                                    return aggregate;
                                },
                                Materialized
                                        .<UUID, WatchTimeAggregate, WindowStore<Bytes, byte[]>>as(
                                                "watch-time-metric-store"
                                        )
                                        .withKeySerde(Serdes.UUID())
                                        .withValueSerde(aggregateSerde)
                        );
        watchTimeMetrics
                .suppress(
                        Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded())
                )
                .toStream()
                .map(this::createSnapShot)
                .foreach(
                        (windowedKey, metric) -> {
                                computedMetricsProducer.publishComputedMetricsEvents(
                                        toComputedMetricEvent(metric)
                                );
                        }
                );


    }

    private KeyValue<Windowed<UUID>, WatchTimeMetric> createSnapShot(
            Windowed<UUID> windowedKey,
            WatchTimeAggregate aggregate
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
        WatchTimeMetric metric = new WatchTimeMetric(
                windowedKey.key(),
                windowStart,
                windowEnd,
                aggregate.getTotalWatchTimeMs(),
                aggregate.getAverageWatchTimeDuration(),
                aggregate.getUniqueSessions(),
                aggregate.getUniqueUsers(),
                OffsetDateTime.now()
        );

        return KeyValue.pair(windowedKey, metric);
    }

    private ComputedMetricEvent toComputedMetricEvent(
            WatchTimeMetric metric
    ) {
        return new ComputedMetricEvent(
                metric.contentId(),
                MetricType.WATCH_TIME,
                metric.windowStart(),
                metric.windowEnd(),
                metric,
                metric.computedAt()
        );
    }


    private boolean isWatchTimeEvent(IngestionType eventType) {
        return eventType == IngestionType.PLAY
                || eventType == IngestionType.PAUSE
                || eventType == IngestionType.SEEK
                || eventType == IngestionType.STOP
                || eventType == IngestionType.COMPLETE;
    }
}
