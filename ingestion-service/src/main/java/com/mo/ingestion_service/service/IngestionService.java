package com.mo.ingestion_service.service;

import com.mo.ingestion_service.dto.request.EngagementIngestionRequest;
import com.mo.ingestion_service.dto.response.IngestionResponse;
import com.mo.ingestion_service.enums.IngestionStatus;
import com.mo.ingestion_service.kafka.producer.IngestionEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngestionService {
    private final RateLimitService rateLimitService;
    private final IdempotencyService idempotencyService;
    private final IngestionEventProducer ingestionEventProducer;

    public IngestionResponse ingest(EngagementIngestionRequest event) {

        rateLimitService.validate(event.userId());

        if (idempotencyService.isDuplicate(event.eventId())) {
            return new IngestionResponse(
                    IngestionStatus.DUPLICATE,
                    event.eventId()
            );
        }

        ingestionEventProducer.publishRawEngagementEvents(event);

        return new IngestionResponse(
                IngestionStatus.ACCEPTED,
                event.eventId()
        );
    }
}
