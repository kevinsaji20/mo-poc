package com.mo.api_gateway.controller;

import com.mo.api_gateway.dto.request.SignupRequest;
import com.mo.api_gateway.dto.response.AuthResponse;
import com.mo.api_gateway.dto.response.SignupResponse;
import com.mo.api_gateway.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.lang.model.type.NullType;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse response = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.SC_CREATED)
                .body(response);
    }
}