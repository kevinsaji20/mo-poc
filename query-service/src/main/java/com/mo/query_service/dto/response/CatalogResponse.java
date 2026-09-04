package com.mo.query_service.dto.response;

import java.util.UUID;

public record CatalogResponse (
        UUID contentId,
        String title,
        String genre
) {
}
