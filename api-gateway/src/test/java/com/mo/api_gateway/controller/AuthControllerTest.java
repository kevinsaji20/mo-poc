package com.mo.api_gateway.controller;

import com.mo.api_gateway.dto.request.LoginRequest;
import com.mo.api_gateway.dto.request.SignupRequest;
import com.mo.api_gateway.dto.request.RequestMetadata;
import com.mo.api_gateway.dto.response.LoginResult;
import com.mo.api_gateway.dto.response.SignupResponse;
import com.mo.api_gateway.dto.response.UserResponse;
import com.mo.api_gateway.enums.AuthProviderType;
import com.mo.api_gateway.service.AuthService;
import com.mo.api_gateway.util.CookieUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private AuthService authService;

    @Mock
    private CookieUtil cookieUtil;

    @BeforeEach
    void setup() {
        AuthController controller = new AuthController(authService, cookieUtil);
        this.webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void signup_shouldReturnCreated() {
        SignupResponse resp = new SignupResponse(
                true,
                "Signup successful, Verification email sent"
        );

        when(authService.signup(any(SignupRequest.class))).thenReturn(resp);

        webTestClient.post().uri("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        BodyInserters.fromValue(
                                new SignupRequest(
                                        "john@example.com",
                                        "John",
                                        "john",
                                        "password123",
                                        AuthProviderType.LOCAL
                                )
                        )
                )
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo(
                        "Signup successful, Verification email sent"
                );

        verify(authService).signup(any(SignupRequest.class));
    }

    @Test
    void login_shouldReturnAuthResponse_andSetCookie() {
        LoginRequest req = new LoginRequest("john@example.com","password123");
        UserResponse user = new UserResponse("John","john","john@example.com", List.of("ANALYTICS_READ"), null);
        LoginResult lr = new LoginResult(true, "Login Successful", user, "access-token", "refresh-token-value");

        when(authService.login(any(LoginRequest.class), any(RequestMetadata.class))).thenReturn(lr);
        doNothing().when(cookieUtil).addAuthCookie(any(), any());

        webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"login\":\"john@example.com\",\"password\":\"password123\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(true)
                .jsonPath("$.accessToken").isEqualTo("access-token")
                .jsonPath("$.user.email").isEqualTo("john@example.com");

        verify(authService).login(any(LoginRequest.class), any(RequestMetadata.class));
        verify(cookieUtil).addAuthCookie(any(), any());
    }

    @Test
    void refresh_shouldReturnAuthResponse_andSetCookie() {
        UserResponse user = new UserResponse("John","john","john@example.com", List.of("ANALYTICS_READ"), null);
        LoginResult lr = new LoginResult(true, "Token Refresh Successful", user, "new-access", "new-refresh");

        when(authService.refresh(anyString(), any(RequestMetadata.class))).thenReturn(lr);
        doNothing().when(cookieUtil).addAuthCookie(any(), any());

        webTestClient.post().uri("/auth/refresh")
                .cookie("refresh_token", "id.secret")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accessToken").isEqualTo("new-access")
                .jsonPath("$.status").isEqualTo(true);

        verify(authService).refresh(anyString(), any(RequestMetadata.class));
        verify(cookieUtil).addAuthCookie(any(), any());
    }

    @Test
    void logout_shouldReturnNoContent_andClearCookie() {
        doNothing().when(cookieUtil).clearAuthCookie(any());
        doNothing().when(authService).logout(anyString(), any(RequestMetadata.class));

        webTestClient.post().uri("/auth/logout")
                .header("Authorization", "Bearer access-token")
                .exchange()
                .expectStatus().isNoContent();

        verify(authService).logout(anyString(), any(RequestMetadata.class));
        verify(cookieUtil).clearAuthCookie(any());
    }
}
