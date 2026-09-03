package com.mo.processing_service.kafka.state.concurrent;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConcurrentViewerAggregate {
    private int peakViewers;
    private long totalViewerSamples;
    private long sampleCount;

    public void record(int currentViewers) {
        peakViewers = Math.max(peakViewers, currentViewers);

        totalViewerSamples += currentViewers;
        sampleCount++;
    }

    public double getAverageViewers() {
        if (sampleCount == 0) {
            return 0;
        }

        return (double) totalViewerSamples / sampleCount;
    }
}
