package com.mo.common.security.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.stream.Collectors;

public class ReactiveJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        return new JwtAuthenticationToken(
                jwt,
                roles.stream()
                        .map(role ->
                                new SimpleGrantedAuthority("ROLE_" + role)
                        )
                        .collect(Collectors.toList()),
                jwt.getSubject()
        );
    }
}