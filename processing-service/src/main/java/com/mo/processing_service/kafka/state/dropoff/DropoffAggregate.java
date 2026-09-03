package com.mo.processing_service.kafka.state.dropoff;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DropoffAggregate {
    private int stopCount;

    public void addStop() {
        stopCount++;
    }
}
