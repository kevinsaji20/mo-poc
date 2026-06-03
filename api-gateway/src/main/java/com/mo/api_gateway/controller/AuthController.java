package com.mo.api_gateway.controller;

import com.mo.api_gateway.dto.request.LoginRequest;
import com.mo.api_gateway.dto.request.SignupRequest;
import com.mo.api_gateway.dto.response.AuthResponse;
import com.mo.api_gateway.dto.response.LoginResult;
import com.mo.api_gateway.dto.response.SignupResponse;
import com.mo.api_gateway.dto.request.RequestMetadata;
import com.mo.api_gateway.service.AuthService;
import com.mo.api_gateway.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse response = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.SC_CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        RequestMetadata metadata =
                new RequestMetadata(
                        httpRequest.getRemoteAddr(),
                        httpRequest.getHeader("User-Agent"),
                        httpRequest.getHeader("X-Device-Id"),
                        httpRequest.getHeader("X-Device-Name")
                );

        LoginResult response = authService.login(request, metadata);

        cookieUtil.addAuthCookie(response, httpResponse);

        return ResponseEntity
                .status(HttpStatus.SC_OK)
                .body(new AuthResponse(
                        response.status(),
                        response.message(),
                        response.accessToken(),
                        response.user()
                ));
    }

    @PostMapping("/refesh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue("refresh_token") String refreshToken,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        RequestMetadata metadata =
                new RequestMetadata(
                        httpRequest.getRemoteAddr(),
                        httpRequest.getHeader("User-Agent"),
                        httpRequest.getHeader("X-Device-Id"),
                        httpRequest.getHeader("X-Device-Name")
                );

        LoginResult response = authService.refresh(refreshToken, metadata);

        cookieUtil.addAuthCookie(response, httpResponse);

        return ResponseEntity
                .status(HttpStatus.SC_OK)
                .body(new AuthResponse(
                        response.status(),
                        response.message(),
                        response.accessToken(),
                        response.user()
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorization,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        String token = authorization.replace("Bearer ", "");
        RequestMetadata metadata =
                new RequestMetadata(
                        httpRequest.getRemoteAddr(),
                        httpRequest.getHeader("User-Agent"),
                        httpRequest.getHeader("X-Device-Id"),
                        httpRequest.getHeader("X-Device-Name")
                );

        authService.logout(token, metadata);

        cookieUtil.clearAuthCookie(httpResponse);

        return ResponseEntity.status(HttpStatus.SC_NO_CONTENT).build();
    }
}