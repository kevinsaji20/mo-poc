package com.mo.query_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "concurrent_viewers_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConcurrentViewersSnapshot {

    @EmbeddedId
    private ConcurrentViewersSnapshotId id;

    @Column(name = "peak_viewers", nullable = false)
    private Integer peakViewers;

    @Column(name = "avg_viewers", nullable = false, precision = 10, scale = 2)
    private BigDecimal avgViewers;

    @Column(name = "computed_at", nullable = false)
    private OffsetDateTime computedAt;
}
