package com.mo.api_gateway.repository;

import com.mo.api_gateway.entity.User;
import com.mo.api_gateway.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmailOrUsername(String email, String username);

    Optional<User>
    findByEmailOrUsernameAndStatusAndEmailVerified(
            String email,
            String username,
            UserStatus status,
            Boolean emailVerified
    );
}
