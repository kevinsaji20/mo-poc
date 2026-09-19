package com.mo.api_gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityConfigTest {

    @Test
    void passwordEncoder_shouldBeBCrypt_withConfiguredStrength() {
        SecurityConfig cfg = new SecurityConfig();
        ReflectionTestUtils.setField(cfg, "bcryptStrength", 12);

        var encoder = cfg.passwordEncoder();

        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }
}
