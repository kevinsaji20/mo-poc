package com.mo.processing_service.kafka.state.completion;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompletionAggregateTest {

    @Test
    void tracksPlayAndCompletionCountsAndRate() {
        CompletionAggregate aggregate = new CompletionAggregate();
        aggregate.addPlay();
        aggregate.addPlay();
        aggregate.addComplete();

        assertThat(aggregate.getPlayCount()).isEqualTo(2);
        assertThat(aggregate.getCompletionCount()).isEqualTo(1);
        assertThat(aggregate.getCompletionRate()).isEqualTo(0.5);
    }
}
