package com.mo.api_gateway.dto.response;

import com.mo.api_gateway.entity.RefreshTokens;

import java.util.UUID;

public record CreateRefreshTokenResponse(
        RefreshTokens entity,
        String refreshToken
) {
}
