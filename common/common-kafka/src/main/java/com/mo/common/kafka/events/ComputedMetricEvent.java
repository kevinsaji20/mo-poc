package com.mo.common.kafka.events;

import com.mo.common.kafka.enums.MetricType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ComputedMetricEvent(
        UUID contentId,
        MetricType metricType,
        OffsetDateTime windowStart,
        OffsetDateTime windowEnd,
        Object value,
        OffsetDateTime computedAt
) {
}
