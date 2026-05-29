package com.mo.api_gateway.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordUtil {
    private final PasswordEncoder passwordEncoder;

    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public Boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
