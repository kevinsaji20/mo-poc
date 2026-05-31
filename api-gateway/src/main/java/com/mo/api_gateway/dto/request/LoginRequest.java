package com.mo.api_gateway.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Email or Username is required")
        String login,

        @NotBlank(message = "Password is required")
        @Size(min = 8)
        String password
) {
}
