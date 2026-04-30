package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.request.CreateUserRequest;
import com.minimaxi.backend.dto.request.InviteUserRequest;
import com.minimaxi.backend.dto.request.UpdateAvatarRequest;
import com.minimaxi.backend.dto.request.UpdateUserRequest;
import com.minimaxi.backend.dto.response.UserResponse;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<UserResponse> getUsers();

    UserResponse getUserById(Long id);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    Map<String, Object> deleteUser(Long id);

    UserResponse inviteUser(InviteUserRequest request);

    UserResponse updateAvatar(Long id, UpdateAvatarRequest request);
}