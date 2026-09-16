package com.mo.processing_service.kafka.state.concurrent;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrentViewerAggregateTest {

    @Test
    void record_tracksPeakAndAverage() {
        ConcurrentViewerAggregate aggregate = new ConcurrentViewerAggregate();

        aggregate.record(2);
        aggregate.record(5);
        aggregate.record(3);

        assertThat(aggregate.getPeakViewers()).isEqualTo(5);
        assertThat(aggregate.getAverageViewers()).isEqualTo((double) 10 / 3);
    }
}
