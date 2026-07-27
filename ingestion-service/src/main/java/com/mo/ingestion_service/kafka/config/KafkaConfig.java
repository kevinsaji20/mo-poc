package com.mo.ingestion_service.kafka.config;

import com.mo.common.kafka.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic rawEngagementEventsTopic() {
        return TopicBuilder
                .name(KafkaTopics.RAW_ENGAGEMENT_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
