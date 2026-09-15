package com.mo.api_gateway.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordUtilTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordUtil passwordUtil;

    @Test
    void hashPassword_delegatesToEncoder() {
        when(passwordEncoder.encode("rawPass")).thenReturn("hashed");

        String hashed = passwordUtil.hashPassword("rawPass");

        assertThat(hashed).isEqualTo("hashed");
        verify(passwordEncoder).encode("rawPass");
    }

    @Test
    void verifyPassword_delegatesToMatches() {
        when(passwordEncoder.matches("raw", "hashed")).thenReturn(true);

        Boolean ok = passwordUtil.verifyPassword("raw", "hashed");

        assertThat(ok).isTrue();
        verify(passwordEncoder).matches("raw", "hashed");
    }
}
