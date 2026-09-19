package com.mo.common.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KeyLoaderTest {

    @Test
    void loadPublicKey_parsesPemAndReturnsPublicKey() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair keyPair = kpg.generateKeyPair();

        String pem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder().encodeToString(((RSAPublicKey) keyPair.getPublic()).getEncoded())
                + "\n-----END PUBLIC KEY-----\n";

        RSAPublicKey publicKey = KeyLoader.loadPublicKey(new ByteArrayResource(pem.getBytes(StandardCharsets.UTF_8)));

        assertThat(publicKey).isNotNull();
        assertThat(publicKey.getModulus()).isEqualTo(((RSAPublicKey) keyPair.getPublic()).getModulus());
    }

    @Test
    void loadPublicKey_throwsForInvalidKey() {
        ByteArrayResource invalid = new ByteArrayResource("not-a-key".getBytes(StandardCharsets.UTF_8));

        assertThrows(Exception.class, () -> KeyLoader.loadPublicKey(invalid));
    }
}
