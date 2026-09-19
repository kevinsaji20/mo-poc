package com.mo.processing_service.kafka.state.concurrent;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrentViewerStateTest {

    @Test
    void activateAndDeactivate_tracksDistinctUsers() {
        ConcurrentViewerState state = new ConcurrentViewerState();
        UUID session1 = UUID.randomUUID();
        UUID session2 = UUID.randomUUID();
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        state.activate(session1, user1);
        state.activate(session2, user1);
        state.activate(UUID.randomUUID(), user2);

        assertThat(state.getDistinctViewerCount()).isEqualTo(2);

        state.deactivate(session2);
        assertThat(state.getDistinctViewerCount()).isEqualTo(2);
    }
}
