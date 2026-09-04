package com.mo.query_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "watch_time_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WatchTimeMetrics {

    @EmbeddedId
    private WatchTimeMetricsId id;

    @Column(name = "total_watch_time_ms", nullable = false)
    private Long totalWatchTimeMs;

    @Column(name = "avg_watch_duration_ms", nullable = false)
    private Long avgWatchDurationMs;

    @Column(name = "unique_sessions", nullable = false)
    private Integer uniqueSessions;

    @Column(name = "unique_users", nullable = false)
    private Integer uniqueUsers;

    @Column(name = "computed_at", nullable = false)
    private OffsetDateTime computedAt;
}
