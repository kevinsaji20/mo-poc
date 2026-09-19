package com.mo.processing_service.kafka.state.watchtime;

import com.mo.common.kafka.enums.IngestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WatchTimeSessionState {
    private UUID sessionId;
    private UUID contentId;
    private UUID userId;

    private long lastPosition;
    private long accumulatedWatchTimeMs;
    private long watchTimeDeltaMs;
    private boolean playing;

    public void process(
            IngestionType eventType,
            long position,
            Long seekFromPosition
    ) {
        this.watchTimeDeltaMs = 0;
        switch (eventType) {
            case PLAY -> handlePlay(position);
            case PAUSE -> handlePause(position);
            case SEEK -> handleSeek(position, seekFromPosition);
            case STOP -> handleStop(position);
            case COMPLETE -> handleComplete(position);
        }
    }

    private void handlePlay(long position) {
        this.lastPosition = position;
        this.playing = true;
    }

    private void handlePause(long position) {
        addWatchTime(position);

        this.lastPosition = position;
        this.playing = false;
    }

    private void handleSeek(long position, Long seekFromPosition) {
        if (seekFromPosition != null) {
            addWatchTime(seekFromPosition);
        }
        this.lastPosition = position;
    }

    private void handleStop(long position) {
        addWatchTime(position);

        this.lastPosition = position;
        this.playing = false;
    }

    private void handleComplete(long position) {
        addWatchTime(position);

        this.lastPosition = position;
        this.playing = false;
    }

    private void addWatchTime(long position) {
        if (!playing) {
            return;
        }

        if (position <= lastPosition) {
            return;
        }

        this.watchTimeDeltaMs = position - lastPosition;
        this.accumulatedWatchTimeMs += this.watchTimeDeltaMs;
    }
}
