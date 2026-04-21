package com.tttn.warehouseqr.modules.auth.service;

import com.tttn.warehouseqr.modules.auth.dto.RegisterRequest;
import com.tttn.warehouseqr.modules.auth.dto.UserCreateRequest;
import com.tttn.warehouseqr.modules.auth.dto.UserResponse;
import com.tttn.warehouseqr.modules.auth.dto.UserUpdateRequest;
import com.tttn.warehouseqr.modules.auth.entity.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

public interface UserService {
    void createUserWithAvatar(UserCreateRequest req, MultipartFile file);
    void updateUserWithAvatar(UserUpdateRequest req, MultipartFile file);
    void register(RegisterRequest req);
    List<UserResponse> getAllUsers();
    void createUser(UserCreateRequest req);
    void updateUser(UserUpdateRequest req);
    UserUpdateRequest getUpdateById(Long id);
    void deleteUser(Long id);
}
