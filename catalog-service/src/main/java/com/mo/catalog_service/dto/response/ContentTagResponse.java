package com.mo.catalog_service.dto.response;

import java.util.UUID;

public record ContentTagResponse(
        UUID id,
        String tag
) {
}
