package com.mo.api_gateway.util;

import com.mo.api_gateway.entity.Role;
import com.mo.api_gateway.entity.User;
import com.mo.api_gateway.entity.UserRoles;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtUtilTest {

    @Test
    void generate_and_validate_token_shouldContainUserInfo() throws Exception {
        JwtUtil jwtUtil = new JwtUtil();

        // generate RSA keys and inject into util
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        PrivateKey privateKey = kp.getPrivate();
        PublicKey publicKey = kp.getPublic();

        ReflectionTestUtils.setField(jwtUtil, "privateKey", privateKey);
        ReflectionTestUtils.setField(jwtUtil, "publicKey", publicKey);
        ReflectionTestUtils.setField(jwtUtil, "authSecretTokenExpiration", 3600L);

        Role role = Role.builder().role("ANALYTICS_READ").build();
        UserRoles ur = UserRoles.builder().role(role).build();
        Set<UserRoles> roles = new HashSet<>();
        roles.add(ur);

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .email("john@example.com")
                .userRoles(roles)
                .build();

        String token = jwtUtil.generateToken(user);

        assertThat(token).isNotNull();

        String extractedUserId = jwtUtil.extractUserId(token);
        String extractedEmail = jwtUtil.extractEmail(token);

        assertThat(extractedUserId).isEqualTo(userId.toString());
        assertThat(extractedEmail).isEqualTo("john@example.com");

        // validateToken should return claims without throwing
        var claims = jwtUtil.validateToken(token);
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
    }
}
