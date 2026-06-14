package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.AppUser;
import com.minimaxi.backend.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

    List<AppUser> findByOrganizationIdAndRoleIn(Long organizationId, List<UserRole> roles);
}