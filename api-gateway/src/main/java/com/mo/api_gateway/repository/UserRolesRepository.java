package com.mo.api_gateway.repository;

import com.mo.api_gateway.entity.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRolesRepository extends JpaRepository<UserRoles, UUID> {

}
