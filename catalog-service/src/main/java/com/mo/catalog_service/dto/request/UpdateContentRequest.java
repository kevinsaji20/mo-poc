package com.mo.catalog_service.dto.request;

import com.mo.catalog_service.enums.ContentStatus;
import com.mo.catalog_service.enums.ContentType;
import com.mo.catalog_service.enums.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.List;

public record UpdateContentRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Content type is required")
        ContentType contentType,

        @NotNull(message = "Genre is required")
        Genre genre,

        @NotBlank(message = "Language is required")
        String language,

        @NotNull(message = "Duration is required")
        @PositiveOrZero(message = "Duration must be positive or zero")
        Integer durationSeconds,

        String thumbnailUrl,

        String streamUrl,

        List<String> tags,

        @NotNull(message = "Release date is required")
        LocalDate releaseDate,

        @NotBlank(message = "Channel name is required")
        String channelName,

        @NotNull(message = "Content Status is required")
        ContentStatus status
) {
}
