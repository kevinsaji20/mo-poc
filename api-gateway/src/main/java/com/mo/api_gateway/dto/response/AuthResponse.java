package com.mo.api_gateway.dto.response;


public record AuthResponse(
        Boolean status,
        String message
) {
}