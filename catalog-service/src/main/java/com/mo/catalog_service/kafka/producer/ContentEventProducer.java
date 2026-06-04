package com.mo.catalog_service.kafka.producer;

import com.mo.catalog_service.kafka.event.ContentArchivedEvent;
import com.mo.catalog_service.kafka.event.ContentPublishedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.content-published}")
    private String contentPublishedTopic;

    @Value("${kafka.topic.content-archived}")
    private String contentArchivedTopic;

    public void publishContentPublished(ContentPublishedEvent event) {
        kafkaTemplate.send(contentPublishedTopic, event.contentId().toString(), event);
    }

    public void  publishContentArchived(ContentArchivedEvent event) {
        kafkaTemplate.send(contentArchivedTopic, event.contentId().toString(), event);
    }
}
