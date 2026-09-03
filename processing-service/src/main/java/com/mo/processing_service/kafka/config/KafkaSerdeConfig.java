package com.mo.processing_service.kafka.config;

import com.mo.processing_service.kafka.state.completion.CompletionAggregate;
import com.mo.processing_service.kafka.state.watchtime.WatchTimeAggregate;
import com.mo.processing_service.kafka.state.watchtime.WatchTimeSessionState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
public class KafkaSerdeConfig {
    @Bean
    public JsonSerde<WatchTimeSessionState> watchTimeSessionStateSerde() {
        return new JsonSerde<>(WatchTimeSessionState.class);
    }

    @Bean
    public JsonSerde<WatchTimeAggregate> watchTimeAggregateSerde() {
        return new JsonSerde<>(WatchTimeAggregate.class);
    }

    @Bean
    public JsonSerde<CompletionAggregate> completionAggregateSerde() {
        return new JsonSerde<>(CompletionAggregate.class);
    }
}
