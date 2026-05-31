package com.mo.api_gateway.dto.response;

public record LoginResult(
        Boolean status,
        String message,
        UserResponse user,
        String accessToken,
        String refreshToken
) {
}
