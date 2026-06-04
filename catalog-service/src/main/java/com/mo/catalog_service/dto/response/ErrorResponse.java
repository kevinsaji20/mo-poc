package com.mo.catalog_service.dto.response;

public record ErrorResponse(
        Boolean status,
        Number statusCode,
        String message
) {
}
