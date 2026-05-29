package com.mo.api_gateway.service;

import com.mo.api_gateway.dto.request.SignupRequest;
import com.mo.api_gateway.dto.response.AuthResponse;
import com.mo.api_gateway.dto.response.SignupResponse;
import com.mo.api_gateway.entity.User;
import com.mo.api_gateway.enums.UserStatus;
import com.mo.api_gateway.repository.UserRepository;
import com.mo.api_gateway.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;

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

        return new SignupResponse(
                true,
                "Signup successful, Verification email sent"
        );
    }


}
