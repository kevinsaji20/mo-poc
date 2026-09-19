package com.mo.processing_service.kafka.state.concurrent;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ConcurrentViewerState {
    private final Map<UUID, UUID> activeSessions = new HashMap<>();

    public void activate(UUID sessionId, UUID userId) {
        activeSessions.put(sessionId, userId);
    }

    public void deactivate(UUID sessionId) {
        activeSessions.remove(sessionId);
    }

    public int getDistinctViewerCount() {
        return (int) activeSessions.values()
                .stream()
                .distinct()
                .count();
    }
}
