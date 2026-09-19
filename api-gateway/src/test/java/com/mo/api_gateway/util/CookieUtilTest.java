package com.mo.api_gateway.util;

import com.mo.api_gateway.dto.response.LoginResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CookieUtilTest {

    private CookieUtil cookieUtil;

    @BeforeEach
    void setup() {
        cookieUtil = new CookieUtil();
        ReflectionTestUtils.setField(cookieUtil, "refreshTokenExpiration", 3600L);
    }

    @Test
    void addAuthCookie_shouldAddRefreshTokenCookie() {
        ServerHttpResponse response = mock(ServerHttpResponse.class);

        LoginResult result = new LoginResult(true, "ok", null, "access", "refresh-token-value");

        ArgumentCaptor<ResponseCookie> captor = ArgumentCaptor.forClass(ResponseCookie.class);

        cookieUtil.addAuthCookie(result, response);

        verify(response).addCookie(captor.capture());

        ResponseCookie cookie = captor.getValue();
        assertThat(cookie.getName()).isEqualTo("refresh_token");
        assertThat(cookie.getValue()).isEqualTo("refresh-token-value");
        assertThat(cookie.getMaxAge()).isEqualTo(java.time.Duration.ofSeconds(3600L));
    }

    @Test
    void clearAuthCookie_shouldAddClearingCookie() {
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        ArgumentCaptor<ResponseCookie> captor = ArgumentCaptor.forClass(ResponseCookie.class);

        cookieUtil.clearAuthCookie(response);

        verify(response).addCookie(captor.capture());

        ResponseCookie cookie = captor.getValue();
        assertThat(cookie.getName()).isEqualTo("refresh_token");
        assertThat(cookie.getMaxAge()).isEqualTo(java.time.Duration.ofSeconds(0L));
    }
}
