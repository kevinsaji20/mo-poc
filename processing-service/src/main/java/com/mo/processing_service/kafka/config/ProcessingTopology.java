package com.mo.processing_service.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.common.kafka.constants.KafkaTopics;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.common.kafka.events.IngestionEvent;
import com.mo.processing_service.kafka.serde.EventEnvelopeSerde;
import com.mo.processing_service.kafka.timestamp.IngestionEventTimestampExtractor;
import com.mo.processing_service.kafka.topology.CompletionRateTopology;
import com.mo.processing_service.kafka.topology.DropoffHeatmapTopology;
import com.mo.processing_service.kafka.topology.WatchTimeTopology;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ProcessingTopology {
    private final ObjectMapper objectMapper;
    private final WatchTimeTopology watchTimeTopology;
    private final CompletionRateTopology completionRateTopology;
    private final DropoffHeatmapTopology dropoffHeatmapTopology;

    @Bean
    public KStream<String, EventEnvelope<IngestionEvent>> stream (
        StreamsBuilder builder
    ) {
        Serde<EventEnvelope<IngestionEvent>> eventEnvelopeSerde =
                new EventEnvelopeSerde<>(
                        objectMapper,
                        IngestionEvent.class
                );

        KStream<String, EventEnvelope<IngestionEvent>> events = builder.stream(
                KafkaTopics.RAW_ENGAGEMENT_EVENTS,
                Consumed.with(
                        Serdes.String(),
                        eventEnvelopeSerde
                ).withTimestampExtractor(
                        new IngestionEventTimestampExtractor()
                )
        );

        watchTimeTopology.build(events);
        completionRateTopology.build(events);
        dropoffHeatmapTopology.build(events);

        return events;
    }
}
