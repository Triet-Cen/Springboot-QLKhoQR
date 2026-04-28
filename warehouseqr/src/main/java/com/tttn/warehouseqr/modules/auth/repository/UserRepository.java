package com.tttn.warehouseqr.modules.auth.repository;

import com.tttn.warehouseqr.modules.auth.dto.UserUpdateRequest;
import com.tttn.warehouseqr.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Dùng Fetch Join để lấy User và Role trong đúng 1 câu lệnh SQL duy nhất
    @Query("SELECT u FROM User u JOIN FETCH u.role")
    List<User> findAllWithRole();

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.status = 'ACTIVE'")
    List<User> findAllActiveUsersWithRole();

    // Kiểm tra cho hàm Create
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByCccd(String cccd);

    // Kiểm tra cho hàm Update (trừ ID hiện tại của chính nó)
    boolean existsByEmailAndUserIdNot(String email, Long userId);
    boolean existsByPhoneAndUserIdNot(String phone, Long userId);
    boolean existsByCccdAndUserIdNot(String cccd, Long userId);
}