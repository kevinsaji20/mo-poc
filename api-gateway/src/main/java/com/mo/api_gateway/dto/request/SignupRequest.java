package com.mo.api_gateway.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @Email
        @NotBlank
        String email,

        @NotBlank
        String name,

        @NotBlank
        String username,

        @NotBlank
        @Size(min=8)
        String password
) {
}
