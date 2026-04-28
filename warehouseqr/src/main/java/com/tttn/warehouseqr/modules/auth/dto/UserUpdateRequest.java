package com.tttn.warehouseqr.modules.auth.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class UserUpdateRequest {
    @NotNull(message = "ID người dùng không được trống")
    private Long userId;

    // Đối với Update, password có thể để trống nếu không đổi,
    // nhưng nếu nhập thì phải đủ 6 ký tự.
    @Pattern(regexp = "^$|^.{6,}$", message = "Mật khẩu mới phải từ 6 ký tự trở lên")
    private String password;

    @NotBlank(message = "Vui lòng nhập họ và tên")
    private String fullName;

    @NotBlank(message = "Vui lòng nhập email")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải đúng 10 số và bắt đầu bằng số 0")
    private String phone;

    @NotBlank(message = "Vui lòng nhập CCCD")
    @Pattern(regexp = "^\\d{12}$", message = "CCCD phải đúng 12 số")
    private String cccd;

    private String gender;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String status;

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

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
}