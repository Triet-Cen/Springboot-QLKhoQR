package com.tttn.warehouseqr.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserCreateRequest {
    @NotBlank(message = "USERNAME_REQUIRED")
    private String username;

    @NotBlank(message = "FULLNAME_REQUIRED")
    private String fullName;

    @Email(message = "EMAIL_INVALID")
    private String email;

    @Size(min = 6, message = "PASSWORD_TOO_SHORT")
    private String password;

    @NotBlank(message = "PHONE_REQUIRED") // Nếu bạn muốn bắt buộc nhập
    @Pattern(regexp = "^(0|\\+84)(\\d{9})$", message = "PHONE_INVALID")
    private String phone;

    private Long roleId;

    // Getter & Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
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