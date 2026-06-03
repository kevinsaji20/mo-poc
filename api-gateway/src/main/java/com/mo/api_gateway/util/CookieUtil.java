package com.mo.api_gateway.util;

import com.mo.api_gateway.dto.response.LoginResult;
import com.mo.api_gateway.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieUtil {
    @Value("${security.jwt.auth-token-expiration}")
    private long authSecretTokenExpiration;

    @Value("${security.refresh-token-expiration}")
    private long refreshTokenExpiration;


    private ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .sameSite("Strict")
                .maxAge(Duration.ofSeconds(refreshTokenExpiration))
                .build();
    }


    private ResponseCookie clearCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
    }

    public void addAuthCookie(LoginResult result, HttpServletResponse httpResponse) {
        ResponseCookie refreshCookie = createRefreshTokenCookie(result.refreshToken());

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    public void clearAuthCookie(HttpServletResponse httpResponse) {
        ResponseCookie refreshCookie = clearCookie("refresh_token");

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
