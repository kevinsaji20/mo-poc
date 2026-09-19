package com.mo.ingestion_service.mapper;

import com.mo.common.kafka.events.IngestionEvent;
import com.mo.ingestion_service.dto.request.EngagementIngestionRequest;
import org.springframework.stereotype.Component;

@Component
public class IngestionEventMapper {
    public IngestionEvent toEvent(EngagementIngestionRequest request) {
        return new IngestionEvent(
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
    }
}
