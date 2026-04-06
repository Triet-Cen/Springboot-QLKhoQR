package com.tttn.warehouseqr.modules.auth.dto;

import com.tttn.warehouseqr.modules.auth.entity.Role;
import jakarta.validation.constraints.*;

public class UserUpdateRequest {
    @NotBlank(message = "ID_REQUIRED")
    private Long userId;

    @NotBlank(message = "FULLNAME_REQUIRED")
    private String fullName;

    @Email(message = "EMAIL_INVALID")
    private String email;

    // Không bắt buộc nhập password khi update, nhưng nếu nhập thì phải đủ độ dài
    @Size(min = 6, message = "PASSWORD_TOO_SHORT")
    private String password;

    @NotBlank(message = "PHONE_REQUIRED") // Nếu bạn muốn bắt buộc nhập
    @Pattern(regexp = "^(0|\\+84)(\\d{9})$", message = "PHONE_INVALID")
    private String phone;
    private Long roleId;

    // Getter & Setter
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
}