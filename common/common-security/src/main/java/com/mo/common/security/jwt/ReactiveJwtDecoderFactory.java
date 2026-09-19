package com.mo.common.security.jwt;

import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

public final class ReactiveJwtDecoderFactory {
    private ReactiveJwtDecoderFactory() {
    }

    public static ReactiveJwtDecoder create(
            Resource publicKey
    ) throws Exception {

        return NimbusReactiveJwtDecoder
                .withPublicKey(
                        KeyLoader.loadPublicKey(publicKey)
                )
                .build();
    }
}
