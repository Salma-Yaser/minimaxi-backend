package com.minimaxi.backend.controller;

import com.minimaxi.backend.config.JwtUtil;
import com.minimaxi.backend.dto.request.CreateUserRequest;
import com.minimaxi.backend.dto.request.InviteUserRequest;
import com.minimaxi.backend.dto.request.UpdateAvatarRequest;
import com.minimaxi.backend.dto.request.UpdateUserRequest;
import com.minimaxi.backend.dto.response.UserResponse;
import com.minimaxi.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    private Long extractOrgId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtUtil.extractOrganizationId(authHeader.substring(7));
        }
        return null;
    }

    @GetMapping
    public List<UserResponse> getUsers(HttpServletRequest request) {
        Long orgId = extractOrgId(request);
        return userService.getUsers(orgId);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/invite")
    public UserResponse inviteUser(@RequestBody InviteUserRequest request) {
        return userService.inviteUser(request);
    }

    @PatchMapping("/{id}/avatar")
    public UserResponse updateAvatar(
            @PathVariable Long id,
            @RequestBody UpdateAvatarRequest request
    ) {
        return userService.updateAvatar(id, request);
    }
}