package com.mo.processing_service.service;

import com.mo.processing_service.entity.WatchTimeMetric;
import com.mo.processing_service.repository.WatchTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchTimeMetricService {
    private final WatchTimeRepository watchTimeRepository;

    public void process(WatchTimeMetric metric) {
        watchTimeRepository.batchUpsert(List.of(metric));
    }
}
