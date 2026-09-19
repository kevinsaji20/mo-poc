package com.mo.common.security.jwt;

import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class KeyLoader {
    private KeyLoader() {}

    public static RSAPublicKey loadPublicKey(Resource resource) throws Exception {
        String key = new String(
                resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8
        );

        key = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        return (RSAPublicKey) KeyFactory
                .getInstance("RSA")
                .generatePublic(
                        new X509EncodedKeySpec(decoded)
                );
    }
}
