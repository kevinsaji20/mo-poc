package com.mo.api_gateway.dto.response;

public record AuthResponse(
        User user,
        String accessToken
) {
}