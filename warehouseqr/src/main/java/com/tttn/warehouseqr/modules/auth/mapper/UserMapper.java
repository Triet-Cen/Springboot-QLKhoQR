package com.tttn.warehouseqr.modules.auth.mapper;

import com.tttn.warehouseqr.modules.auth.dto.*;
import com.tttn.warehouseqr.modules.auth.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // Từ Entity sang Response (để hiển thị danh sách)
    public UserResponse toResponse(User user) {
        if (user == null) return null;
        UserResponse res = new UserResponse();
        res.setUserId(user.getUserId());
        res.setUsername(user.getUsername());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        if (user.getRole() != null) {
            res.setRoleName(user.getRole().getRoleName());
        }
        return res;
    }

    // Từ Entity sang UpdateRequest (để đổ dữ liệu vào Form Update)
    public UserUpdateRequest toUpdateRequest(User user) {
        if (user == null) return null;
        UserUpdateRequest req = new UserUpdateRequest();
        req.setUserId(user.getUserId());
        req.setFullName(user.getFullName());
        req.setEmail(user.getEmail());
        req.setPhone(user.getPhone());
        if (user.getRole() != null) {
            req.setRoleId(user.getRole().getRoleId());
        }
        return req;
    }

    // Map từ Request vào Entity đang tồn tại (Dùng cho hàm Update)
    public void updateUserFromRequest(UserUpdateRequest req, User user) {
        if (req == null || user == null) return;

        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        // Password và Role nên xử lý riêng ở Service để đảm bảo bảo mật/logic DB
    }

    public User toEntity(UserCreateRequest request) {
        if (request == null) return null;
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        return user;
    }
}