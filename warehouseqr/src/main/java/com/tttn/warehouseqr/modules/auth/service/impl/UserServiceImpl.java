package com.tttn.warehouseqr.modules.auth.service.impl;

import com.tttn.warehouseqr.common.exception.AppException;
import com.tttn.warehouseqr.common.exception.ErrorCode;
import com.tttn.warehouseqr.modules.auth.dto.*;
import com.tttn.warehouseqr.modules.auth.entity.Role;
import com.tttn.warehouseqr.modules.auth.entity.User;
import com.tttn.warehouseqr.modules.auth.mapper.UserMapper;
import com.tttn.warehouseqr.modules.auth.repository.RoleRepository;
import com.tttn.warehouseqr.modules.auth.repository.UserRepository;
import com.tttn.warehouseqr.modules.auth.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final String UPLOAD_DIR = "src/main/resources/static/assets/images/avatars/";

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllActiveUsersWithRole().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void register(RegisterRequest req) {
        validateUniqueFields(req.getUsername(), req.getEmail(), req.getPhone(), req.getCccd());
        User user = userMapper.toEntity(req);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        Role defaultRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.setRole(defaultRole);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void createUserWithAvatar(UserCreateRequest req, MultipartFile file) {
        validateUniqueFields(req.getUsername(), req.getEmail(), req.getPhone(), req.getCccd());

        // 2. Map Entity
        User user = userMapper.toEntity(req);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        // 3. Xử lý Role
        Role role = roleRepository.findById(req.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.setRole(role);

        // 4. Lưu ảnh (Hàm saveImage đã có try-catch bên trong rồi, không cần bọc lại ở đây)
        if (file != null && !file.isEmpty()) {
            user.setAvatar(saveImage(file));
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserWithAvatar(UserUpdateRequest req, MultipartFile file) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Kiểm tra trùng Email khi cập nhật
        if (!user.getEmail().equals(req.getEmail())) {
            if (userRepository.existsByEmailAndUserIdNot(req.getEmail(), req.getUserId())) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
        }

        // 3. Kiểm tra trùng SĐT khi cập nhật
        if (!user.getPhone().equals(req.getPhone())) {
            if (userRepository.existsByPhoneAndUserIdNot(req.getPhone(), req.getUserId())) {
                throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
            }
        }

        if (!user.getCccd().equals(req.getCccd())) {
            if (userRepository.existsByCccdAndUserIdNot(req.getCccd(), req.getUserId())) {
                throw new AppException(ErrorCode.CCCD_ALREADY_EXISTS);
            }
        }

        userMapper.updateUserFromRequest(req, user);

        if (req.getRoleId() != null) {
            Role role = roleRepository.findById(req.getRoleId())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            user.setRole(role);
        }

        if (req.getPassword() != null && !req.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        if (file != null && !file.isEmpty()) {
            user.setAvatar(saveImage(file));
        }
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setStatus("INACTIVE");
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public UserUpdateRequest getUpdateById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUpdateRequest(user);
    }

    // --- Helper Methods ---

    private void validateUniqueFields(String username, String email, String phone, String cccd) {
        if (userRepository.existsByUsername(username)) throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        if (userRepository.existsByEmail(email)) throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        if (userRepository.existsByPhone(phone)) throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
        if (userRepository.existsByCccd(cccd)) throw new AppException(ErrorCode.CCCD_ALREADY_EXISTS);
    }

    private String saveImage(MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu file ảnh: " + e.getMessage());
        }
    }

    // Các hàm createUser và updateUser cũ không dùng ảnh có thể gọi qua hàm WithAvatar với file = null
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

        userMapper.updateUserFromRequest(req, user);

        // Chỉ mã hóa và cập nhật nếu người dùng có nhập mật khẩu mới
        if (req.getPassword() != null && !req.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        Role role = roleRepository.findById(req.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.setRole(role);

        userRepository.save(user);
    }
}