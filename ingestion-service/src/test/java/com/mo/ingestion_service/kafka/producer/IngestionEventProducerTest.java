package com.mo.ingestion_service.kafka.producer;

import com.mo.common.kafka.constants.KafkaTopics;
import com.mo.common.kafka.enums.DeviceType;
import com.mo.common.kafka.enums.EventType;
import com.mo.common.kafka.enums.IngestionType;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.common.kafka.events.IngestionEvent;
import com.mo.ingestion_service.dto.request.EngagementIngestionRequest;
import com.mo.ingestion_service.mapper.IngestionEventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngestionEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private IngestionEventMapper ingestionEventMapper;

    @InjectMocks
    private IngestionEventProducer ingestionEventProducer;

    @Test
    void publishRawEngagementEvents_mapsAndSendsEnvelopeToKafka() {
        EngagementIngestionRequest request = new EngagementIngestionRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                IngestionType.PLAY,
                100L,
                25000L,
                OffsetDateTime.now(),
                UUID.randomUUID(),
                DeviceType.MOBILE,
                "US",
                0L,
                100L
        );
        IngestionEvent mappedEvent = new IngestionEvent(
                request.eventId(),
                request.contentId(),
                request.userId(),
                request.eventType(),
                request.playbackPositionMs(),
                request.totalDurationMs(),
                request.eventTimeStamp(),
                request.sessionId(),
                request.deviceType(),
                request.region(),
                request.seekFromPosition(),
                request.seekToPosition()
        );

        when(ingestionEventMapper.toEvent(request)).thenReturn(mappedEvent);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

        ingestionEventProducer.publishRawEngagementEvents(request);

        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(eq(KafkaTopics.RAW_ENGAGEMENT_EVENTS), anyString(), payloadCaptor.capture());

        Object sentPayload = payloadCaptor.getValue();
        assertThat(sentPayload).isInstanceOf(EventEnvelope.class);
        EventEnvelope<?> envelope = (EventEnvelope<?>) sentPayload;
        assertThat(envelope.eventType()).isEqualTo(EventType.RAW_ENGAGEMENT_EVENTS);
        assertThat(envelope.payload()).isEqualTo(mappedEvent);
    }
}
