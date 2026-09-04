package com.mo.query_service.repository;

import com.mo.query_service.entity.ConcurrentViewersSnapshot;
import com.mo.query_service.entity.ConcurrentViewersSnapshotId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcurrentViewersSnapshotRepository
        extends JpaRepository<ConcurrentViewersSnapshot, ConcurrentViewersSnapshotId> {
}
