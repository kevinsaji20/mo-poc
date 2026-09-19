package com.mo.processing_service.kafka.state.watchtime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WatchTimeContribution {
    private UUID sessionId;
    private UUID contentId;
    private UUID userId;
    private long watchTimeMs;
}
