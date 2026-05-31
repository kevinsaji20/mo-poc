package com.mo.api_gateway.repository;

import com.mo.api_gateway.entity.Role;
import com.mo.api_gateway.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByRole(RoleType role);
}