package com.mo.catalog_service.kafka.config;

import com.mo.common.kafka.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic contentPublishedTopic() {
        return TopicBuilder
                .name(KafkaTopics.CONTENT_PUBLISHED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic contentArchivedTopic() {
        return TopicBuilder
                .name(KafkaTopics.CONTENT_ARCHIVED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
