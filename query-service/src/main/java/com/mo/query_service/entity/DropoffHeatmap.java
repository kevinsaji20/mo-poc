package com.mo.query_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "dropoff_heatmap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DropoffHeatmap {

    @EmbeddedId
    private DropoffHeatmapId id;

    @Column(name = "window_end", nullable = false)
    private OffsetDateTime windowEnd;

    @Column(name = "stop_count", nullable = false)
    private Integer stopCount;

    @Column(name = "computed_at", nullable = false)
    private OffsetDateTime computedAt;
}