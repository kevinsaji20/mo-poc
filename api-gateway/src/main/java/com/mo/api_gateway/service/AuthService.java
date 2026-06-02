package com.mo.api_gateway.service;

import com.mo.api_gateway.dto.request.*;
import com.mo.api_gateway.dto.response.*;
import com.mo.api_gateway.entity.RefreshTokens;
import com.mo.api_gateway.entity.Role;
import com.mo.api_gateway.entity.User;
import com.mo.api_gateway.entity.UserRoles;
import com.mo.api_gateway.dto.request.RequestMetadata;
import com.mo.api_gateway.enums.RoleType;
import com.mo.api_gateway.enums.UserStatus;
import com.mo.api_gateway.repository.RefreshTokenRepository;
import com.mo.api_gateway.repository.RoleRepository;
import com.mo.api_gateway.repository.UserRepository;
import com.mo.api_gateway.repository.UserRolesRepository;
import com.mo.api_gateway.util.JwtUtil;
import com.mo.api_gateway.util.PasswordUtil;
import com.mo.api_gateway.util.RefreshTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRolesRepository userRolesRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtUtil jwtUtil;
    private final RefreshTokenUtil refreshTokenUtil;
    private final PasswordUtil passwordUtil;

    @Value("${security.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if(userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email Already exist");
        }

        if(userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exist");
        }

        if(request.password() == null || request.password().isBlank()) {
            throw new RuntimeException("Password is required");
        }

        User user = new User();

        user.setUsername(request.username());
        user.setFullName(request.fullName());
        user.setEmail(request.email());

        user.setPasswordHash(passwordUtil.hashPassword(request.password()));
        user.setEmailVerified(false);

        user.setStatus(UserStatus.ACTIVE);
        user.setProvider(request.provider());

        user.setProviderId(null);
        user.setProfilePicture(null);

        userRepository.save(user);

        Role role = roleRepository
                .findByRole(RoleType.ANALYTICS_READ)
                .orElseThrow(() -> new IllegalStateException("Internal Server Error"));

        UserRoles assignment = new UserRoles();

        assignment.setUser(user);
        assignment.setRole(role);

        userRolesRepository.save(assignment);

        return new SignupResponse(
                true,
                "Signup successful, Verification email sent"
        );
    }

    private User authenticateUser(LoginRequest request) {
        User user = userRepository
                .findByEmailOrUsernameAndStatusAndEmailVerified(
                        request.login(),
                        request.login(),
                        UserStatus.ACTIVE,
                        true
                )
                .orElseThrow(() ->
                        new RuntimeException("Invalid credentials")
                );

        if (!passwordUtil.verifyPassword(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new RuntimeException("Invalid credentials");
        }
        return user;
    }

    private CreateRefreshTokenResponse createRefreshToken(
            User user,
            RequestMetadata metadata,
            String accessToken
    ) {
        UUID tokenId = UUID.randomUUID();

        String refreshToken = refreshTokenUtil.generateRefreshToken();
        RefreshTokens entity = new RefreshTokens();
        entity.setUser(user);
        entity.setTokenHash(passwordUtil.hashPassword(refreshToken));
        entity.setTokenId(tokenId);
        entity.setDeviceId(metadata.deviceId());
        entity.setDeviceName(metadata.deviceName());
        entity.setIpAddress(metadata.ipAddress());
        entity.setUserAgent(metadata.userAgent());
        entity.setExpiresAt(OffsetDateTime.now().plusSeconds(refreshTokenExpiration));
        entity.setLastUsedAt(OffsetDateTime.now());
        refreshTokenRepository.save(entity);

        return new CreateRefreshTokenResponse(entity, tokenId.toString() + '.' + refreshToken);
    }

    private UserResponse mapToUserResponse(
            User user
    ) {
        return new UserResponse(
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRoles()
                        .stream()
                        .map(userRole ->
                                userRole.getRole().getRole()
                        )
                        .toList(),
                user.getProfilePicture()
        );
    }

    private void updateLastLogin(User user) {
        user.setLastLoginAt(OffsetDateTime.now().toLocalDateTime());
        userRepository.save(user);
    }

    @Transactional
    public LoginResult login(LoginRequest request, RequestMetadata metadata) {
        User user = authenticateUser(request);

        updateLastLogin(user);

        String accessToken = jwtUtil.generateToken(user);

        CreateRefreshTokenResponse refreshTokenResponse = createRefreshToken(user, metadata, accessToken);

        UserResponse userResponse = mapToUserResponse(user);

        return new LoginResult(
                true,
                "Login Successful",
                userResponse,
                accessToken,
                refreshTokenResponse.refreshToken()
        );
    }

    public LoginResult refresh(String refreshToken, RequestMetadata metadata) {
        String[] parts = refreshToken.split("\\.", 2);
        UUID tokenId = UUID.fromString(parts[0]);
        String secret = parts[1];

        RefreshTokens token = refreshTokenRepository
                .findByTokenIdAndIsRevokedFalse(tokenId)
                .orElseThrow(() -> new RuntimeException("Invalid Refresh token"));

        if(!passwordUtil.verifyPassword(secret, token.getTokenHash())) {
            throw new RuntimeException("Invalid Refresh token");
        }

        if(token.getExpiresAt().isBefore(OffsetDateTime.now())){
            throw new RuntimeException("Refresh token has expired");
        }

        User user = token.getUser();

        // Revoke Current Refresh token
        token.setIsRevoked(true);
        token.setRevokedAt(OffsetDateTime.now());

        // Generate new Access Token
        String newAccessToken = jwtUtil.generateToken(user);

        // Generate new Refresh token
        CreateRefreshTokenResponse newRefreshTokenResponse = createRefreshToken(user, metadata, newAccessToken);

        token.setReplacedByToken(newRefreshTokenResponse.entity());

        UserResponse userResponse = mapToUserResponse(user);

        return new LoginResult(
                true,
                "Token Refresh Successful",
                userResponse,
                newAccessToken,
                newRefreshTokenResponse.refreshToken()
        );
    }

    public void logout(String refreshToken) {
        String[] parts = refreshToken.split("\\.", 2);
        UUID tokenId = UUID.fromString(parts[0]);

        RefreshTokens token = refreshTokenRepository
                .findByTokenIdAndIsRevokedFalse(tokenId)
                .orElseThrow(() -> new RuntimeException("Invalid Refresh token"));

        token.setIsRevoked(true);
        token.setRevokedAt(OffsetDateTime.now());
    }
}
