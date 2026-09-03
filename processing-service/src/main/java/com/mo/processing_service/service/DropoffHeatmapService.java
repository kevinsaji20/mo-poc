package com.mo.processing_service.service;

import com.mo.processing_service.entity.DropoffHeatmap;
import com.mo.processing_service.repository.DropoffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DropoffHeatmapService {
    private final DropoffRepository dropoffRepository;

    public void process(DropoffHeatmap metric) {
        dropoffRepository.batchUpsert(
                List.of(metric)
        );
    }
}
