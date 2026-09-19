package com.mo.ingestion_service.mapper;

import com.mo.common.kafka.enums.DeviceType;
import com.mo.common.kafka.enums.IngestionType;
import com.mo.common.kafka.events.IngestionEvent;
import com.mo.ingestion_service.dto.request.EngagementIngestionRequest;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IngestionEventMapperTest {

    @Test
    void toEvent_mapsRequestToEvent() {
        UUID eventId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        OffsetDateTime timestamp = OffsetDateTime.now();

        EngagementIngestionRequest request = new EngagementIngestionRequest(
                eventId,
                contentId,
                userId,
                IngestionType.COMPLETE,
                1500L,
                30000L,
                timestamp,
                sessionId,
                DeviceType.SMART_TV,
                "US",
                1000L,
                1500L
        );

        IngestionEventMapper mapper = new IngestionEventMapper();

        IngestionEvent event = mapper.toEvent(request);

        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.contentId()).isEqualTo(contentId);
        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.eventType()).isEqualTo(IngestionType.COMPLETE);
        assertThat(event.playbackPositionMs()).isEqualTo(1500L);
        assertThat(event.totalDurationMs()).isEqualTo(30000L);
        assertThat(event.eventTimeStamp()).isEqualTo(timestamp);
        assertThat(event.sessionId()).isEqualTo(sessionId);
        assertThat(event.deviceType()).isEqualTo(DeviceType.SMART_TV);
        assertThat(event.region()).isEqualTo("US");
        assertThat(event.seekFromPosition()).isEqualTo(1000L);
        assertThat(event.seekToPosition()).isEqualTo(1500L);
    }
}
