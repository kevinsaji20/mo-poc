package com.mo.api_gateway.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class RefreshTokenUtil {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generateRefreshToken() {
        byte[] bytes = new byte[64];

        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
