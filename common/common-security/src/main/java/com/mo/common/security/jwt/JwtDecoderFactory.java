package com.mo.common.security.jwt;

import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

public final class JwtDecoderFactory {
    private JwtDecoderFactory() {}

    public static JwtDecoder create(
            Resource publicKey
    ) throws Exception {

        return NimbusJwtDecoder
                .withPublicKey(
                        KeyLoader.loadPublicKey(publicKey)
                )
                .build();
    }
}
