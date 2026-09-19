package com.mo.processing_service.service;

import com.mo.processing_service.entity.ConcurrentViewerSnapshot;
import com.mo.processing_service.repository.ConcurrentViewerRepository;
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
class ConcurrentViewerServiceTest {

    @Mock
    private ConcurrentViewerRepository concurrentViewerRepository;

    @InjectMocks
    private ConcurrentViewerService service;

    @Test
    void process_batchesUpsertForMetric() {
        ConcurrentViewerSnapshot metric = new ConcurrentViewerSnapshot(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                15,
                8.5,
                OffsetDateTime.now()
        );

        service.process(metric);

        verify(concurrentViewerRepository).batchUpsert(List.of(metric));
    }
}
