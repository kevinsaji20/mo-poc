package com.mo.ingestion_service.service;

import com.mo.common.kafka.enums.DeviceType;
import com.mo.common.kafka.enums.IngestionType;
import com.mo.ingestion_service.dto.request.EngagementIngestionRequest;
import com.mo.ingestion_service.dto.response.IngestionResponse;
import com.mo.ingestion_service.enums.IngestionStatus;
import com.mo.ingestion_service.kafka.producer.IngestionEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngestionServiceTest {

    @Mock
    private RateLimitService rateLimitService;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private IngestionEventProducer ingestionEventProducer;

    @InjectMocks
    private IngestionService ingestionService;

    @Test
    void ingest_whenWithinLimitAndNotDuplicate_publishesAndReturnsAccepted() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        EngagementIngestionRequest request = new EngagementIngestionRequest(
                eventId,
                UUID.randomUUID(),
                userId,
                IngestionType.PLAY,
                1000L,
                25000L,
                OffsetDateTime.now(),
                UUID.randomUUID(),
                DeviceType.MOBILE,
                "US",
                0L,
                1000L
        );

        when(idempotencyService.isDuplicate(eventId)).thenReturn(false);

        IngestionResponse response = ingestionService.ingest(request);

        assertThat(response.status()).isEqualTo(IngestionStatus.ACCEPTED);
        assertThat(response.eventId()).isEqualTo(eventId);
        verify(rateLimitService).validate(userId);
        verify(ingestionEventProducer).publishRawEngagementEvents(request);
    }

    @Test
    void ingest_whenDuplicate_returnsDuplicateAndSkipsPublish() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        EngagementIngestionRequest request = new EngagementIngestionRequest(
                eventId,
                UUID.randomUUID(),
                userId,
                IngestionType.PAUSE,
                2000L,
                40000L,
                OffsetDateTime.now(),
                UUID.randomUUID(),
                DeviceType.DESKTOP,
                "CA",
                200L,
                1000L
        );

        when(idempotencyService.isDuplicate(eventId)).thenReturn(true);

        IngestionResponse response = ingestionService.ingest(request);

        assertThat(response.status()).isEqualTo(IngestionStatus.DUPLICATE);
        assertThat(response.eventId()).isEqualTo(eventId);
        verify(rateLimitService).validate(userId);
        verify(ingestionEventProducer, never()).publishRawEngagementEvents(request);
    }
}
