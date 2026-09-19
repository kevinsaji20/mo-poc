package com.mo.query_service.kafka.consumer;

import com.mo.common.kafka.constants.KafkaConsumers;
import com.mo.common.kafka.constants.KafkaTopics;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.common.kafka.events.ContentArchivedEvent;
import com.mo.query_service.cache.MetricsCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentArchivedConsumer {
    private final MetricsCacheService metricsCacheService;

    @KafkaListener(
            topics = KafkaTopics.CONTENT_ARCHIVED,
            groupId = KafkaConsumers.QUERY_SERVICE
    )
    public void consume(EventEnvelope<ContentArchivedEvent> envelope) {
        ContentArchivedEvent event = envelope.payload();
        metricsCacheService.evictContent(event.contentId());
    }
}
