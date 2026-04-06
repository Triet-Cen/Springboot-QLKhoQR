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


}