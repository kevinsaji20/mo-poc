package com.mo.api_gateway.dto.request;

import com.mo.api_gateway.dto.response.AuthResponse;
import com.mo.api_gateway.enums.AuthProviderType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.checkerframework.common.value.qual.EnumVal;

public record SignupRequest(
        @Email
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Name is required")
        String fullName,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min=8)
        String password,

        @NotBlank(message = "AuthProviderType is required")
        AuthProviderType provider
) {
}
