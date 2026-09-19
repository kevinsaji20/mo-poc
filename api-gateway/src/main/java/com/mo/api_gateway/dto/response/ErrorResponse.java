package com.mo.api_gateway.dto.response;

public record ErrorResponse(
        Boolean status,
        Number statusCode,
        String message
) {
}
