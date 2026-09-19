package com.mo.processing_service.kafka.state.dropoff;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DropoffAggregateTest {

    @Test
    void addStop_incrementsCounter() {
        DropoffAggregate aggregate = new DropoffAggregate();

        aggregate.addStop();
        aggregate.addStop();

        assertThat(aggregate.getStopCount()).isEqualTo(2);
    }
}
