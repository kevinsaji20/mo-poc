package com.mo.api_gateway.dto.response;

public record SignupResponse(
        Boolean success,
        String message
) {
}