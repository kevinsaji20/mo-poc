package com.mo.query_service.repository;

import com.mo.query_service.entity.DropoffHeatmap;
import com.mo.query_service.entity.DropoffHeatmapId;
import com.mo.query_service.projections.DropoffProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DropoffHeatmapRepository
        extends JpaRepository<DropoffHeatmap, DropoffHeatmapId> {
    @Query(value = """
    SELECT
        position_bucket AS positionBucket,
        sum(stop_count) AS stopCount
    FROM dropoff_heatmap
    WHERE content_id = :contentId
        AND window_start >= :from
        AND window_end <= :to
    GROUP BY position_bucket
    ORDER BY position_bucket
    """, nativeQuery = true)
    List<DropoffProjection> findDropoff(
            @Param("contentId") UUID contentId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );
}
