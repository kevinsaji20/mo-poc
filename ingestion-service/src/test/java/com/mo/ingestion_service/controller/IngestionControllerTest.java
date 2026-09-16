package com.mo.ingestion_service.controller;

import com.mo.common.kafka.enums.DeviceType;
import com.mo.common.kafka.enums.IngestionType;
import com.mo.ingestion_service.dto.request.BackEngagementIngestionRequest;
import com.mo.ingestion_service.dto.request.EngagementIngestionRequest;
import com.mo.ingestion_service.dto.response.IngestionResponse;
import com.mo.ingestion_service.enums.IngestionStatus;
import com.mo.ingestion_service.service.IngestionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngestionControllerTest {

    @Mock
    private IngestionService ingestionService;

    @InjectMocks
    private IngestionController ingestionController;

    @Test
    void submitIngestion_returnsAcceptedResponse() {
        UUID eventId = UUID.randomUUID();
        EngagementIngestionRequest request = new EngagementIngestionRequest(
                eventId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                IngestionType.PLAY,
                0L,
                5000L,
                OffsetDateTime.now(),
                UUID.randomUUID(),
                DeviceType.MOBILE,
                "US",
                0L,
                100L
        );
        IngestionResponse expected = new IngestionResponse(IngestionStatus.ACCEPTED, eventId);
        when(ingestionService.ingest(request)).thenReturn(expected);

        var response = ingestionController.submitIngestion(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void submitBatch_returnsAllResponses() {
        UUID firstEventId = UUID.randomUUID();
        UUID secondEventId = UUID.randomUUID();
        EngagementIngestionRequest first = new EngagementIngestionRequest(
                firstEventId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                IngestionType.PAUSE,
                10L,
                2000L,
                OffsetDateTime.now(),
                UUID.randomUUID(),
                DeviceType.DESKTOP,
                "CA",
                0L,
                10L
        );
        EngagementIngestionRequest second = new EngagementIngestionRequest(
                secondEventId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                IngestionType.STOP,
                100L,
                4000L,
                OffsetDateTime.now(),
                UUID.randomUUID(),
                DeviceType.SMART_TV,
                "GB",
                20L,
                100L
        );
        BackEngagementIngestionRequest request = new BackEngagementIngestionRequest(List.of(first, second));
        when(ingestionService.ingest(first)).thenReturn(new IngestionResponse(IngestionStatus.ACCEPTED, firstEventId));
        when(ingestionService.ingest(second)).thenReturn(new IngestionResponse(IngestionStatus.ACCEPTED, secondEventId));

        var response = ingestionController.submitBatch(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactly(
                new IngestionResponse(IngestionStatus.ACCEPTED, firstEventId),
                new IngestionResponse(IngestionStatus.ACCEPTED, secondEventId)
        );
    }
}
