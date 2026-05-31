package com.mo.api_gateway.enums;

public record RequestMetadata(
        String ipAddress,
        String userAgent,
        String deviceId,
        String deviceName
) {
}
