package com.tttn.warehouseqr.modules.auth.service;

import com.tttn.warehouseqr.modules.auth.dto.UserCreateRequest;
import com.tttn.warehouseqr.modules.auth.dto.UserResponse;
import com.tttn.warehouseqr.modules.auth.dto.UserUpdateRequest;
import com.tttn.warehouseqr.modules.auth.entity.Role;

import java.util.List;
import java.util.stream.Collectors;

public interface UserService {
    List<UserResponse> getAllUsers();
    void createUser(UserCreateRequest req);
    void updateUser(UserUpdateRequest req);
    UserUpdateRequest getUpdateById(Long id);
    void deleteUser(Long id);
}
