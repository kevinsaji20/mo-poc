package com.mo.query_service.repository;

import com.mo.query_service.dto.response.SummaryResponse;
import com.mo.query_service.projections.MetricsSummaryProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface MetricsSummaryRepository {
    public SummaryResponse getSummary(
            UUID contentId,
            OffsetDateTime from,
            OffsetDateTime to
    );
}
