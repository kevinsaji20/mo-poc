package com.mo.processing_service.service;

import com.mo.processing_service.entity.WatchTimeMetric;
import com.mo.processing_service.repository.WatchTimeRepository;
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
class WatchTimeMetricServiceTest {

    @Mock
    private WatchTimeRepository watchTimeRepository;

    @InjectMocks
    private WatchTimeMetricService service;

    @Test
    void process_batchesUpsertForMetric() {
        WatchTimeMetric metric = new WatchTimeMetric(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                12000L,
                600L,
                20,
                12,
                OffsetDateTime.now()
        );

        service.process(metric);

        verify(watchTimeRepository).batchUpsert(List.of(metric));
    }
}
