package com.mo.common.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class JwtDecoderFactoryTest {

    @Test
    void create_buildsDecoderFromPublicKeyResource() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair keyPair = kpg.generateKeyPair();

        String pem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder().encodeToString(((RSAPublicKey) keyPair.getPublic()).getEncoded())
                + "\n-----END PUBLIC KEY-----\n";

        JwtDecoder decoder = JwtDecoderFactory.create(new ByteArrayResource(pem.getBytes(StandardCharsets.UTF_8)));

        assertThat(decoder).isNotNull();
        assertThat(decoder).isInstanceOf(NimbusJwtDecoder.class);
    }
}
