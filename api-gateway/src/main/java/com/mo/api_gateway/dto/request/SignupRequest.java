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
        @NotBlank
        String email,

        @NotBlank
        String fullName,

        @NotBlank
        String username,

        @NotBlank
        @Size(min=8)
        String password,

        @NotBlank
        AuthProviderType provider
) {
}
