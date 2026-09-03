package com.mo.processing_service.service;

import com.mo.processing_service.entity.CompletionMetric;
import com.mo.processing_service.repository.CompletionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompletionRateService {
    private final CompletionRepository completionRepository;

    public void process(CompletionMetric metric) {
        completionRepository.batchUpsert(
                List.of(metric)
        );
    }
}
