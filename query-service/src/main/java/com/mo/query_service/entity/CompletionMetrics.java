package com.mo.query_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.EmbeddedId;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.OffsetDateTime;

@Entity
@Table(name = "completion_metrics")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompletionMetrics {
    @EmbeddedId
    private CompletionMetricsId id;

    @Column(name = "play_count", nullable = false)
    private Integer playCount;

    @Column(name = "complete_count", nullable = false)
    private Integer completeCount;

    @Column(name = "completion_rate", nullable = false, precision = 5, scale = 2)
    private double completionRate;

    @Column(name = "computed_at", nullable = false)
    private OffsetDateTime computedAt;
}
