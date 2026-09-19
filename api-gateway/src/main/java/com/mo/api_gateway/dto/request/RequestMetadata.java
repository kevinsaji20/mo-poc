package com.mo.api_gateway.dto.request;

public record RequestMetadata(
        String ipAddress,
        String userAgent,
        String deviceId,
        String deviceName
) {
}
