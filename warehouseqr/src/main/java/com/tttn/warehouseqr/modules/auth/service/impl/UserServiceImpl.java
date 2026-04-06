package com.tttn.warehouseqr.modules.auth.service.impl;

import com.tttn.warehouseqr.common.exception.AppException;
import com.tttn.warehouseqr.common.exception.ErrorCode;
import com.tttn.warehouseqr.modules.auth.dto.UserCreateRequest;
import com.tttn.warehouseqr.modules.auth.dto.UserResponse;
import com.tttn.warehouseqr.modules.auth.dto.UserUpdateRequest;
import com.tttn.warehouseqr.modules.auth.entity.Role;
import com.tttn.warehouseqr.modules.auth.entity.User;
import com.tttn.warehouseqr.modules.auth.mapper.UserMapper;
import com.tttn.warehouseqr.modules.auth.repository.RoleRepository;
import com.tttn.warehouseqr.modules.auth.repository.UserRepository;
import com.tttn.warehouseqr.modules.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    // PHẢI CÓ FINAL để RequiredArgsConstructor hoạt động
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllWithRole().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createUser(UserCreateRequest req) {
        if(userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        Role role = roleRepository.findById(req.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = userMapper.toEntity(req);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(UserUpdateRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());

        // Chỉ mã hóa và cập nhật nếu người dùng có nhập mật khẩu mới
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        Role role = roleRepository.findById(req.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.setRole(role);

        userRepository.save(user);
    }

    @Override
    public UserUpdateRequest getUpdateById(Long id) {
        // 1. Tìm user trong DB
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Map từ Entity sang DTO để Controller trả về View
        return userMapper.toUpdateRequest(user);
    }
}