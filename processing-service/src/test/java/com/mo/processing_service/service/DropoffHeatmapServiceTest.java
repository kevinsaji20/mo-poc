package com.mo.processing_service.service;

import com.mo.processing_service.entity.DropoffHeatmap;
import com.mo.processing_service.repository.DropoffRepository;
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
class DropoffHeatmapServiceTest {

    @Mock
    private DropoffRepository dropoffRepository;

    @InjectMocks
    private DropoffHeatmapService service;

    @Test
    void process_batchesUpsertForMetric() {
        DropoffHeatmap metric = new DropoffHeatmap(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                (short) 30,
                5,
                OffsetDateTime.now()
        );

        service.process(metric);

        verify(dropoffRepository).batchUpsert(List.of(metric));
    }
}
