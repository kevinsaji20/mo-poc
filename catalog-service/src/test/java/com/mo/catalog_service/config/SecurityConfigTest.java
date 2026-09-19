package com.mo.catalog_service.config;

import com.mo.common.security.jwt.JwtDecoderFactory;
import com.mo.common.security.jwt.JwtAuthenticationConverter;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void jwtAuthenticationConverter_returnsInstance() {
        SecurityConfig cfg = new SecurityConfig();
        JwtAuthenticationConverter conv = cfg.jwtAuthenticationConverter();
        assertThat(conv).isNotNull();
        assertThat(conv).isInstanceOf(JwtAuthenticationConverter.class);
    }

    @Test
    void jwtDecoder_usesFactory_create() throws Exception {
        SecurityConfig cfg = new SecurityConfig();

        Resource mockResource = Mockito.mock(Resource.class);
        ReflectionTestUtils.setField(cfg, "publicKeyResource", mockResource);

        JwtDecoder mockDecoder = Mockito.mock(JwtDecoder.class);

        try (MockedStatic<JwtDecoderFactory> utilities = Mockito.mockStatic(JwtDecoderFactory.class)) {
            utilities.when(() -> JwtDecoderFactory.create(mockResource)).thenReturn(mockDecoder);

            JwtDecoder decoder = cfg.jwtDecoder();
            assertThat(decoder).isSameAs(mockDecoder);

            utilities.verify(() -> JwtDecoderFactory.create(mockResource));
        }
    }
}
