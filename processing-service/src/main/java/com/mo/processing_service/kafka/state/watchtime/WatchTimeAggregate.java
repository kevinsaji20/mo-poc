package com.mo.processing_service.kafka.state.watchtime;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class WatchTimeAggregate {
    private long totalWatchTimeMs;

    private final Set<UUID> sessionIds = new HashSet<>();
    private final Set<UUID> userIds = new HashSet<>();

    public void add(
            WatchTimeContribution contribution
    ) {
        this.totalWatchTimeMs += contribution.getWatchTimeMs();

        if (contribution.getSessionId() != null) {
            sessionIds.add(contribution.getSessionId());
        }

        if (contribution.getUserId() != null) {
            userIds.add(contribution.getUserId());
        }
    }

    public int getUniqueSessions() {
        return sessionIds.size();
    }

    public int getUniqueUsers() {
        return userIds.size();
    }

    public long getAverageWatchTimeDuration() {
        if (sessionIds.isEmpty()) {
            return 0;
        }
        return totalWatchTimeMs / sessionIds.size();
    }
}
