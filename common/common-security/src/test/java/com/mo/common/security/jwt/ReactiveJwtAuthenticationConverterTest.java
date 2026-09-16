package com.mo.common.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReactiveJwtAuthenticationConverterTest {

    @Test
    void convert_mapsRolesToGrantedAuthorities() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claims(claims -> claims.putAll(Map.of("roles", List.of("USER", "ADMIN"))))
                .subject("user-123")
                .build();

        var auth = converter.convert(jwt);

        assertThat(auth).isInstanceOf(JwtAuthenticationToken.class);
        assertThat(auth.getName()).isEqualTo("user-123");
        assertThat(auth.getAuthorities()).contains(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
    }
}
