package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.request.CreateUserRequest;
import com.minimaxi.backend.dto.request.InviteUserRequest;
import com.minimaxi.backend.dto.request.UpdateAvatarRequest;
import com.minimaxi.backend.dto.request.UpdateUserRequest;
import com.minimaxi.backend.dto.response.UserResponse;
import com.minimaxi.backend.entity.AppUser;
import com.minimaxi.backend.enums.UserRole;
import com.minimaxi.backend.enums.UserStatus;
import com.minimaxi.backend.repository.AppUserRepository;
import com.minimaxi.backend.repository.OrganizationRepository;
import com.minimaxi.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final AppUserRepository appUserRepository;
    private final OrganizationRepository organizationRepository;

    public UserServiceImpl(AppUserRepository appUserRepository,
                           OrganizationRepository organizationRepository) {
        this.appUserRepository = appUserRepository;
        this.organizationRepository = organizationRepository;
    }

    // ─── helper: entity → response ───────────────────────────────────────────
    private UserResponse toResponse(AppUser user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name().toLowerCase() : null)
                .firstLogin(user.getStatus() == UserStatus.INVITED)
                .companyId(user.getOrganization() != null ? user.getOrganization().getId() : null)
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .status(user.getStatus() != null ? user.getStatus().name().toLowerCase() : null)
                .avatar(user.getAvatar())
                .build();
    }

    // ─── GET ALL ─────────────────────────────────────────────────────────────

    @Override
    public List<UserResponse> getUsers() {
        return appUserRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ─── GET BY ID ───────────────────────────────────────────────────────────

    @Override
    public UserResponse getUserById(Long id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toResponse(user);
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        AppUser user = new AppUser();
        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        user.setStatus(UserStatus.INVITED);
        user.setCreatedAt(Instant.now());

        if (request.getOrganizationId() != null) {
            user.setOrganization(
                    organizationRepository.findById(request.getOrganizationId())
                            .orElseThrow(() -> new RuntimeException("Organization not found"))
            );
        }

        return toResponse(appUserRepository.save(user));
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (request.getName() != null)   user.setFullName(request.getName());
        if (request.getPhone() != null)  user.setPhone(request.getPhone());
        if (request.getRole() != null)   user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        if (request.getStatus() != null) user.setStatus(UserStatus.valueOf(request.getStatus().toUpperCase()));

        return toResponse(appUserRepository.save(user));
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────
/*
    @Override
    public Map<String, Object> deleteUser(Long id) {
        if (!appUserRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        appUserRepository.deleteById(id);
        return Map.of("success", true);
    }*/
    @Override
    public Map<String, Object> deleteUser(Long id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setStatus(UserStatus.DISABLED);
        appUserRepository.save(user);
        return Map.of("success", true);
    }

    // ─── INVITE ──────────────────────────────────────────────────────────────
    // بيعمل نفس الـ createUser بس بـ status INVITED
    // في الـ real flow هتبعتي email، دلوقتي بنحفظ في DB بس

    @Override
    public UserResponse inviteUser(InviteUserRequest request) {
        AppUser user = new AppUser();
        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        user.setStatus(UserStatus.INVITED);
        user.setCreatedAt(Instant.now());

        if (request.getOrganizationId() != null) {
            user.setOrganization(
                    organizationRepository.findById(request.getOrganizationId())
                            .orElseThrow(() -> new RuntimeException("Organization not found"))
            );
        }

        return toResponse(appUserRepository.save(user));
    }

    // ─── UPDATE AVATAR ───────────────────────────────────────────────────────
    // بيحفظ الـ base64 image في الـ avatar column

    @Override
    public UserResponse updateAvatar(Long id, UpdateAvatarRequest request) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setAvatar(request.getAvatar());

        return toResponse(appUserRepository.save(user));
    }
}