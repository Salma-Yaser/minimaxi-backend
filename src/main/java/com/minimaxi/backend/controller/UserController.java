package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.request.CreateUserRequest;
import com.minimaxi.backend.dto.request.InviteUserRequest;
import com.minimaxi.backend.dto.request.UpdateAvatarRequest;
import com.minimaxi.backend.dto.request.UpdateUserRequest;
import com.minimaxi.backend.dto.response.UserResponse;
import com.minimaxi.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Frontend calls: GET /api/users  ✅
    @GetMapping
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }

    // Frontend calls: GET /api/users/{id}  ✅
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Frontend calls: POST /api/users  ✅
    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    // Frontend calls: PUT /api/users/{id}  ✅
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ) {
        return userService.updateUser(id, request);
    }

    // Frontend calls: DELETE /api/users/{id}  ✅
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    // Frontend calls: POST /api/users/invite  ✅
    @PostMapping("/invite")
    public UserResponse inviteUser(@RequestBody InviteUserRequest request) {
        return userService.inviteUser(request);
    }

    // Frontend calls: PATCH /api/users/{id}/avatar  ✅
    @PatchMapping("/{id}/avatar")
    public UserResponse updateAvatar(
            @PathVariable Long id,
            @RequestBody UpdateAvatarRequest request
    ) {
        return userService.updateAvatar(id, request);
    }
}