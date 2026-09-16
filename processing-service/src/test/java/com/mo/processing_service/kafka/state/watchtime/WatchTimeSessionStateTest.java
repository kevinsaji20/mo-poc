package com.mo.processing_service.kafka.state.watchtime;

import com.mo.common.kafka.enums.IngestionType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WatchTimeSessionStateTest {

    @Test
    void process_tracksAccumulatedWatchTimeAcrossPlayPauseAndComplete() {
        WatchTimeSessionState state = new WatchTimeSessionState();
        UUID sessionId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        state.setSessionId(sessionId);
        state.setContentId(contentId);
        state.setUserId(userId);

        state.process(IngestionType.PLAY, 0L, null);
        state.process(IngestionType.PAUSE, 500L, null);
        state.process(IngestionType.PLAY, 500L, null);
        state.process(IngestionType.COMPLETE, 2000L, null);

        assertThat(state.isPlaying()).isFalse();
        assertThat(state.getAccumulatedWatchTimeMs()).isEqualTo(2000L);
        assertThat(state.getWatchTimeDeltaMs()).isEqualTo(1500L);
    }
}
