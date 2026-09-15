package com.mo.api_gateway.service;

import com.mo.api_gateway.dto.request.LoginRequest;
import com.mo.api_gateway.dto.request.RequestMetadata;
import com.mo.api_gateway.dto.request.SignupRequest;
import com.mo.api_gateway.dto.response.LoginResult;
import com.mo.api_gateway.dto.response.SignupResponse;
import com.mo.api_gateway.entity.RefreshTokens;
import com.mo.api_gateway.entity.Role;
import com.mo.api_gateway.entity.User;
import com.mo.api_gateway.entity.UserRoles;
import com.mo.api_gateway.enums.AuthProviderType;
import com.mo.api_gateway.enums.RoleType;
import com.mo.api_gateway.enums.UserStatus;
import com.mo.api_gateway.repository.RefreshTokenRepository;
import com.mo.api_gateway.repository.RoleRepository;
import com.mo.api_gateway.repository.UserRepository;
import com.mo.api_gateway.repository.UserRolesRepository;
import com.mo.api_gateway.util.JwtUtil;
import com.mo.api_gateway.util.PasswordUtil;
import com.mo.api_gateway.util.RefreshTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRolesRepository userRolesRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenUtil refreshTokenUtil;

    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                authService,
                "refreshTokenExpiration",
                604800L
        );
    }

    // Existing signup tests
    @Test
    void signup_shouldCreateUserSuccessfully() {
        SignupRequest request = new SignupRequest(
                "john@example.com",
                "John Doe",
                "john",
                "password123",
                AuthProviderType.LOCAL
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordUtil.hashPassword(request.password())).thenReturn("hashed-password");

        Role role = new Role();
        role.setRole(RoleType.ANALYTICS_READ.name());

        when(roleRepository.findByRole(RoleType.ANALYTICS_READ)).thenReturn(Optional.of(role));

        SignupResponse response = authService.signup(request);

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("Signup successful, Verification email sent");

        // Verify persistence
        verify(userRepository).save(any(User.class));
        verify(userRolesRepository).save(any(UserRoles.class));
    }

    @Test
    void signup_shouldRejectExistingEmail() {

        SignupRequest request = new SignupRequest(
                "john@example.com",
                "John Doe",
                "john",
                "password123",
                AuthProviderType.LOCAL
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.signup(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Email Already exist");
        verify(userRepository, never()).save(any(User.class));
        verify(userRolesRepository, never()).save(any(UserRoles.class));
        verify(passwordUtil, never()).hashPassword(anyString());
    }

    @Test
    void signup_shouldRejectExistingUsername() {

        SignupRequest request = new SignupRequest(
                "john@example.com",
                "John Doe",
                "john",
                "password123",
                AuthProviderType.LOCAL
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.signup(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Username already exist");

        verify(userRepository, never()).save(any(User.class));
        verify(userRolesRepository, never()).save(any(UserRoles.class));
        verify(passwordUtil, never()).hashPassword(anyString());
    }

    @Test
    void signup_shouldRejectBlankPassword() {

        SignupRequest request = new SignupRequest(
                "john@example.com",
                "John Doe",
                "john",
                "   ",
                AuthProviderType.LOCAL
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.signup(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Password is required");

        verify(passwordUtil, never()).hashPassword(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signup_shouldFailWhenDefaultRoleDoesNotExist() {

        SignupRequest request = new SignupRequest(
                "john@example.com",
                "John Doe",
                "john",
                "password123",
                AuthProviderType.LOCAL
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordUtil.hashPassword(request.password())).thenReturn("hashed-password");
        when(roleRepository.findByRole(RoleType.ANALYTICS_READ)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> authService.signup(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Internal Server Error");
        verify(userRepository).save(any(User.class));
        verify(userRolesRepository, never()).save(any(UserRoles.class));
    }

    // Added tests for login/refresh/logout flows

    @Test
    void login_shouldReturnTokensAndUserResponse_onValidCredentials() {
        // prepare
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("john@example.com");
        user.setFullName("John Doe");
        user.setUsername("john");
        user.setPasswordHash("hashed-pass");
        UserRoles ur = new UserRoles();
        Role r = new Role();
        r.setRole("ANALYTICS_READ");
        ur.setRole(r);
        user.setUserRoles(Set.of(ur));

        LoginRequest request = new LoginRequest("john@example.com", "password123");
        RequestMetadata metadata = new RequestMetadata("1.2.3.4", "ua", "dev-1", "device");

        when(userRepository.findByEmailOrUsernameAndStatusAndEmailVerified(
                request.login(), request.login(), UserStatus.ACTIVE, true
        )).thenReturn(Optional.of(user));

        when(passwordUtil.verifyPassword(request.password(), user.getPasswordHash())).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("access-token-xyz");
        when(refreshTokenUtil.generateRefreshToken()).thenReturn("refresh-secret");
        when(passwordUtil.hashPassword("refresh-secret")).thenReturn("hashed-refresh");

        // act
        LoginResult result = authService.login(request, metadata);

        // assert
        assertThat(result.status()).isTrue();
        assertThat(result.accessToken()).isEqualTo("access-token-xyz");
        assertThat(result.refreshToken()).contains("refresh-secret");

        verify(userRepository, atLeastOnce()).save(user); // updateLastLogin
        verify(refreshTokenRepository).save(any(RefreshTokens.class));
    }

    @Test
    void login_shouldThrow_whenInvalidPassword() {
        User user = new User();
        user.setPasswordHash("hashed-pass");

        LoginRequest request = new LoginRequest("john@example.com", "wrongpass");
        RequestMetadata metadata = new RequestMetadata("1.2.3.4", "ua", "dev-1", "device");

        when(userRepository.findByEmailOrUsernameAndStatusAndEmailVerified(
                anyString(), anyString(), any(), anyBoolean()
        )).thenReturn(Optional.of(user));

        when(passwordUtil.verifyPassword(request.password(), user.getPasswordHash())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request, metadata));
        assertThat(ex.getMessage()).isEqualTo("Invalid credentials");

        verify(jwtUtil, never()).generateToken(any());
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void refresh_shouldReturnNewTokens_whenValid() {
        // prepare
        UUID tokenId = UUID.randomUUID();
        String secret = "old-secret";
        RefreshTokens token = new RefreshTokens();
        token.setTokenId(tokenId);
        token.setTokenHash("hashed-old");
        token.setExpiresAt(OffsetDateTime.now().plusSeconds(1000));
        User user = new User();
        user.setId(UUID.randomUUID());
        token.setUser(user);

        when(refreshTokenRepository.findByTokenIdAndIsRevokedFalse(tokenId)).thenReturn(Optional.of(token));
        when(passwordUtil.verifyPassword(secret, token.getTokenHash())).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("new-access");
        when(refreshTokenUtil.generateRefreshToken()).thenReturn("new-secret");
        when(passwordUtil.hashPassword("new-secret")).thenReturn("hashed-new");

        RequestMetadata metadata = new RequestMetadata("1.2.3.4", "ua", "dev-1", "device");

        // construct input refresh token string as tokenId.secret
        String refreshTokenInput = tokenId.toString() + "." + secret;

        // act
        LoginResult result = authService.refresh(refreshTokenInput, metadata);

        // assert
        assertThat(result.status()).isTrue();
        assertThat(result.accessToken()).isEqualTo("new-access");
        assertThat(result.refreshToken()).contains("new-secret");

        // verify rotation saved
        verify(refreshTokenRepository).save(token);
        verify(refreshTokenRepository, atLeastOnce()).save(any(RefreshTokens.class));
    }

    @Test
    void refresh_shouldThrow_whenTokenNotFound() {
        UUID tokenId = UUID.randomUUID();
        String input = tokenId.toString() + ".secret";

        when(refreshTokenRepository.findByTokenIdAndIsRevokedFalse(tokenId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.refresh(input, new RequestMetadata("ip","ua","d","n")));
        assertThat(ex.getMessage()).isEqualTo("Invalid Refresh token");
    }

    @Test
    void refresh_shouldThrow_whenSecretDoesNotMatch() {
        UUID tokenId = UUID.randomUUID();
        String input = tokenId.toString() + ".secret";
        RefreshTokens token = new RefreshTokens();
        token.setTokenId(tokenId);
        token.setTokenHash("hashed");
        token.setExpiresAt(OffsetDateTime.now().plusSeconds(1000));

        when(refreshTokenRepository.findByTokenIdAndIsRevokedFalse(tokenId)).thenReturn(Optional.of(token));
        when(passwordUtil.verifyPassword("secret", "hashed")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.refresh(input, new RequestMetadata("ip","ua","d","n")));
        assertThat(ex.getMessage()).isEqualTo("Invalid Refresh token");
    }

    @Test
    void refresh_shouldThrow_whenExpired() {
        UUID tokenId = UUID.randomUUID();
        String input = tokenId.toString() + ".secret";
        RefreshTokens token = new RefreshTokens();
        token.setTokenId(tokenId);
        token.setTokenHash("hashed");
        token.setExpiresAt(OffsetDateTime.now().minusSeconds(10));

        when(refreshTokenRepository.findByTokenIdAndIsRevokedFalse(tokenId)).thenReturn(Optional.of(token));
        when(passwordUtil.verifyPassword("secret", "hashed")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.refresh(input, new RequestMetadata("ip","ua","d","n")));
        assertThat(ex.getMessage()).isEqualTo("Refresh token has expired");
    }

    @Test
    void logout_shouldRevokeToken_whenFound() {
        UUID uid = UUID.randomUUID();
        User user = new User();
        user.setId(uid);
        String accessToken = "access-token";

        RefreshTokens token = new RefreshTokens();
        token.setUser(user);
        token.setIsRevoked(false);

        // mock jwt extraction
        when(jwtUtil.extractUserId(accessToken)).thenReturn(uid.toString());
        when(refreshTokenRepository.findByUserIdAndDeviceIdAndIsRevokedFalse(uid, "dev-1")).thenReturn(Optional.of(token));

        RequestMetadata metadata = new RequestMetadata("ip","ua","dev-1","d");

        authService.logout(accessToken, metadata);

        ArgumentCaptor<RefreshTokens> captor = ArgumentCaptor.forClass(RefreshTokens.class);
        verify(refreshTokenRepository).save(captor.capture());

        RefreshTokens saved = captor.getValue();
        assertThat(saved.getIsRevoked()).isTrue();
        assertThat(saved.getRevokedAt()).isNotNull();
    }

    @Test
    void logout_shouldThrow_whenNotFound() {
        UUID uid = UUID.randomUUID();
        String accessToken = "access-token";
        when(jwtUtil.extractUserId(accessToken)).thenReturn(uid.toString());
        when(refreshTokenRepository.findByUserIdAndDeviceIdAndIsRevokedFalse(uid, "dev-1")).thenReturn(Optional.empty());

        RequestMetadata metadata = new RequestMetadata("ip","ua","dev-1","d");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.logout(accessToken, metadata));
        assertThat(ex.getMessage()).isEqualTo("Invalid Refresh token");
    }
}
