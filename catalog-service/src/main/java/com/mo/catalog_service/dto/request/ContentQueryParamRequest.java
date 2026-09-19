package com.mo.catalog_service.dto.request;

import com.mo.catalog_service.enums.ContentStatus;
import com.mo.catalog_service.enums.ContentType;
import com.mo.catalog_service.enums.Genre;

public record ContentQueryParamRequest(
    Genre genre,
    ContentType contentType,
    ContentStatus contentStatus
) {
}
