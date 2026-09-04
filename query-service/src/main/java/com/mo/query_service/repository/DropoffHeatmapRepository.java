package com.mo.query_service.repository;

import com.mo.query_service.entity.DropoffHeatmap;
import com.mo.query_service.entity.DropoffHeatmapId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DropoffHeatmapRepository
        extends JpaRepository<DropoffHeatmap, DropoffHeatmapId> {
}
