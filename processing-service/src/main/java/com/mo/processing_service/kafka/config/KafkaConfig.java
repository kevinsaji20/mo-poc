package com.mo.processing_service.kafka.config;

import com.mo.common.kafka.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaConfig {
    @Bean
    public NewTopic computedMetricsEventTopic() {
        return TopicBuilder
                .name(KafkaTopics.COMPUTED_METRICS_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
