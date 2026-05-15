package com.mo.api_gateway.dto.response;

public record User (
        String name,
        String username,
        String email,
        String[] roles,
        String profilePicture
) {}