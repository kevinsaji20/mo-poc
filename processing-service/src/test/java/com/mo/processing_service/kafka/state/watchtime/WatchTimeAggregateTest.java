package com.mo.processing_service.kafka.state.watchtime;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WatchTimeAggregateTest {

    @Test
    void add_tracksTotalUniqueCountsAndAverage() {
        WatchTimeAggregate aggregate = new WatchTimeAggregate();

        aggregate.add(new WatchTimeContribution(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 1000L));
        aggregate.add(new WatchTimeContribution(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 500L));

        assertThat(aggregate.getTotalWatchTimeMs()).isEqualTo(1500L);
        assertThat(aggregate.getUniqueSessions()).isEqualTo(2);
        assertThat(aggregate.getUniqueUsers()).isEqualTo(2);
        assertThat(aggregate.getAverageWatchTimeDuration()).isEqualTo(750L);
    }
}
