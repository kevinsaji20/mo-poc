package com.mo.api_gateway.controller;

import com.mo.api_gateway.dto.request.LoginRequest;
import com.mo.api_gateway.dto.request.SignupRequest;
import com.mo.api_gateway.dto.response.AuthResponse;
import com.mo.api_gateway.dto.response.LoginResult;
import com.mo.api_gateway.dto.response.SignupResponse;
import com.mo.api_gateway.dto.request.RequestMetadata;
import com.mo.api_gateway.service.AuthService;
import com.mo.api_gateway.util.CookieUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    private RequestMetadata buildMetadata(
            ServerHttpRequest request
    ) {
        return new RequestMetadata(
                request.getRemoteAddress() != null
                        ? request.getRemoteAddress()
                        .getAddress()
                        .getHostAddress()
                        : null,
                request.getHeaders().getFirst("User-Agent"),
                request.getHeaders().getFirst("X-Device-Id"),
                request.getHeaders().getFirst("X-Device-Name")
        );
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse response = authService.signup(request);

        return Mono.just(
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response)
        );
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            ServerHttpRequest httpRequest,
            ServerHttpResponse httpResponse
    ) {
        RequestMetadata metadata = buildMetadata(httpRequest);

        LoginResult response = authService.login(request, metadata);

        cookieUtil.addAuthCookie(response, httpResponse);

        return Mono.just(
                ResponseEntity
                .status(HttpStatus.OK)
                .body(new AuthResponse(
                        response.status(),
                        response.message(),
                        response.accessToken(),
                        response.user()
                ))
        );
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<AuthResponse>> refresh(
            @CookieValue("refresh_token") String refreshToken,
            ServerHttpRequest httpRequest,
            ServerHttpResponse httpResponse
    ) {
        RequestMetadata metadata = buildMetadata(httpRequest);

        LoginResult response = authService.refresh(refreshToken, metadata);

        cookieUtil.addAuthCookie(response, httpResponse);

        return Mono.just(ResponseEntity
                .status(HttpStatus.OK)
                .body(new AuthResponse(
                        response.status(),
                        response.message(),
                        response.accessToken(),
                        response.user()
                ))
        );
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(
            @RequestHeader("Authorization") String authorization,
            ServerHttpRequest httpRequest,
            ServerHttpResponse httpResponse
    ) {
        String token = authorization.replace("Bearer ", "");
        RequestMetadata metadata = buildMetadata(httpRequest);

        authService.logout(token, metadata);

        cookieUtil.clearAuthCookie(httpResponse);

        return Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }
}