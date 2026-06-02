package com.mo.api_gateway.util;

import com.mo.api_gateway.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${security.jwt.auth-token-expiration}")
    private long authSecretTokenExpiration;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    private PrivateKey loadPrivateKey() throws Exception {
        ClassPathResource resource = new ClassPathResource("keys/private.pem");

        String key = new String(resource.getInputStream().readAllBytes());
        String content =
                key.replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s", "");

        byte[] privateBytes = Base64.getDecoder().decode(content);

        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
    }

    private PublicKey loadPublicKey() throws Exception {
        ClassPathResource resource = new ClassPathResource("keys/public.pem");

        String key = new String(resource.getInputStream().readAllBytes());
        String content =
                key.replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");

        byte[] publicBytes = Base64.getDecoder().decode(content);
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicBytes));
    }

    @PostConstruct
    private void init() {
        try {
            this.privateKey = loadPrivateKey();
            this.publicKey = loadPublicKey();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to load JWT keys", exception);
        }
    }

    public String generateToken(User user) {
        Instant now = Instant.now();

        return Jwts
                .builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getUserRoles()
                        .stream()
                        .map(userRoles ->
                                userRoles
                                        .getRole()
                                        .getRole()
                        )
                        .toList()
                )
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(
                        Date.from(now.plusSeconds(authSecretTokenExpiration))
                )
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserId(String token) {
        return validateToken(token).getSubject();
    }

    public String extractEmail(String token) {
        return validateToken(token).get("email", String.class);
    }
}
