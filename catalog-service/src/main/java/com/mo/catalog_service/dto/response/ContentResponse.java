package com.mo.catalog_service.dto.response;

import com.mo.catalog_service.entity.ContentTag;
import com.mo.catalog_service.enums.ContentStatus;
import com.mo.catalog_service.enums.ContentType;
import com.mo.catalog_service.enums.Genre;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ContentResponse(
        UUID contentId,
        String title,
        String description,
        ContentType contentType,
        Genre genre,
        String language,
        Integer durationSeconds,
        String thumbnailUrl,
        String streamUrl,
        List<ContentTag> tags,
        LocalDate releaseDate,
        String channelName,
        ContentStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        UUID createdBy
) {
}
