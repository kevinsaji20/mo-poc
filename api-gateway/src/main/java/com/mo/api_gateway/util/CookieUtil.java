package com.mo.api_gateway.util;

import com.mo.api_gateway.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtil {
    @Value("${security.jwt.auth-token-expiration}")
    private long authSecretTokenExpiration;

    @Value("${security.refresh-token-expiration")
    private long refreshTokenExpiration;

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofSeconds(authSecretTokenExpiration))
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofSeconds(refreshTokenExpiration))
                .build();
    }

    public ResponseCookie createUserDetailsCookie(UserResponse user) {
        return ResponseCookie.from("user", user.toString())
                .sameSite("strict")
                .build();
    }

    public ResponseCookie clearCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
    }
}
