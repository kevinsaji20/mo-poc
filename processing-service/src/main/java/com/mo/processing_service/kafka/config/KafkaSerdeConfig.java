package com.mo.processing_service.kafka.config;

import com.mo.processing_service.kafka.state.WatchTimeSessionState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
public class KafkaSerdeConfig {

    @Bean
    public JsonSerde<WatchTimeSessionState> watchTimeSessionStateSerde() {
        return new JsonSerde<>(WatchTimeSessionState.class);
    }
    
}
