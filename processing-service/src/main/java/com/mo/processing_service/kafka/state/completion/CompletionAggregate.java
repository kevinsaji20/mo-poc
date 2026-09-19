package com.mo.processing_service.kafka.state.completion;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class CompletionAggregate {
    private int playCount;
    private int completionCount;

    public void addPlay() {
        playCount++;
    }

    public void addComplete() {
        completionCount++;
    }

    public double getCompletionRate() {
        if (playCount == 0) {
            return 0;
        }

        return (double) completionCount / playCount;
    }
}
