package com.mo.api_gateway.dto.response;

import java.util.List;

public record UserResponse(
        String name,
        String username,
        String email,
        List<String> roles,
        String profilePicture
) {}