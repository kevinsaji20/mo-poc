package com.mo.api_gateway.controller;

import com.mo.api_gateway.dto.request.SignupRequest;
import com.mo.api_gateway.dto.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

//    @PostMapping("/signup")
//    public ResponseEntity<AuthResponse> signup(
//            @Valid @RequestBody SignupRequest request
//    ) {
//
//    }
}