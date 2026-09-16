package com.mo.processing_service.service;

import com.mo.processing_service.entity.CompletionMetric;
import com.mo.processing_service.repository.CompletionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompletionRateServiceTest {

    @Mock
    private CompletionRepository completionRepository;

    @InjectMocks
    private CompletionRateService service;

    @Test
    void process_batchesUpsertForMetric() {
        CompletionMetric metric = new CompletionMetric(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                10,
                7,
                0.7,
                OffsetDateTime.now()
        );

        service.process(metric);

        verify(completionRepository).batchUpsert(List.of(metric));
    }
}
