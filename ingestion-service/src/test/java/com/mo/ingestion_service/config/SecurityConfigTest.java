package com.mo.ingestion_service.config;

import com.mo.common.security.jwt.JwtAuthenticationConverter;
import com.mo.common.security.jwt.JwtDecoderFactory;
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
        SecurityConfig config = new SecurityConfig();

        JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();

        assertThat(converter).isNotNull();
        assertThat(converter).isInstanceOf(JwtAuthenticationConverter.class);
    }

    @Test
    void jwtDecoder_usesFactoryCreate() throws Exception {
        SecurityConfig config = new SecurityConfig();
        Resource mockResource = Mockito.mock(Resource.class);
        ReflectionTestUtils.setField(config, "publicKeyResource", mockResource);

        JwtDecoder mockDecoder = Mockito.mock(JwtDecoder.class);

        try (MockedStatic<JwtDecoderFactory> mocked = Mockito.mockStatic(JwtDecoderFactory.class)) {
            mocked.when(() -> JwtDecoderFactory.create(mockResource)).thenReturn(mockDecoder);

            JwtDecoder decoder = config.jwtDecoder();

            assertThat(decoder).isSameAs(mockDecoder);
            mocked.verify(() -> JwtDecoderFactory.create(mockResource));
        }
    }
}
