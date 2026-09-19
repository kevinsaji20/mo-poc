package com.mo.processing_service.service;

import com.mo.processing_service.entity.ConcurrentViewerSnapshot;
import com.mo.processing_service.repository.ConcurrentViewerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcurrentViewerService {
    private final ConcurrentViewerRepository concurrentViewerRepository;

    public void process(ConcurrentViewerSnapshot metric) {
        concurrentViewerRepository.batchUpsert(
                List.of(metric)
        );
    }
}
