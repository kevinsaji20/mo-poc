package com.mo.api_gateway.util;

import com.mo.api_gateway.dto.response.LoginResult;
import com.mo.api_gateway.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtil {
    @Value("${security.jwt.auth-token-expiration}")
    private long authSecretTokenExpiration;

    @Value("${security.refresh-token-expiration")
    private long refreshTokenExpiration;

    private ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofSeconds(authSecretTokenExpiration))
                .build();
    }

    private ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/auth")
                .sameSite("Strict")
                .maxAge(Duration.ofSeconds(refreshTokenExpiration))
                .build();
    }

    private ResponseCookie createUserDetailsCookie(UserResponse user) {
        return ResponseCookie.from("user", user.toString())
                .sameSite("strict")
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
        ResponseCookie accessCookie = createAccessTokenCookie(result.accessToken());
        ResponseCookie refreshCookie = createRefreshTokenCookie(result.refreshToken());
        ResponseCookie userCookie = createUserDetailsCookie(result.user());

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, userCookie.toString());
    }

    public void clearAuthCookie(HttpServletResponse httpResponse) {
        ResponseCookie accessCookie = clearCookie("access_token");
        ResponseCookie refreshCookie = clearCookie("refresh_token");
        ResponseCookie userCookie = clearCookie("user");

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, userCookie.toString());
    }
}
