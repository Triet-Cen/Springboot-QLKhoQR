package com.tttn.warehouseqr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tạm thời disable để test cho nhanh, nếu chạy rồi thì bật lại sau
                .authorizeHttpRequests(auth -> auth
                        // 1. Công khai (Public)
                        .requestMatchers("/","/product-detail/**","/product/**","/css/**", "/js/**", "/img/**", "/auth/**").permitAll()
                        .requestMatchers("/manager/purchase-orders/**").permitAll() // Cho phép xem/tạo đơn mua hàng công khai

                        // 2. Quản trị hệ thống (Chỉ ADMIN)
                        .requestMatchers("/admin/users/**").hasRole("ADMIN")

                        // 3. Phê duyệt & Tài chính (ADMIN và MANAGER)
                        .requestMatchers("/api/sales-orders/*/approve").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/sales-orders/*/rollback-payment").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/manager/approvals/**", "/api/manager/orders/**").hasAnyRole("ADMIN", "MANAGER")

                        // 4. Quản lý danh mục & Thiết lập kho (ADMIN và MANAGER)
                        // Staff không nên có quyền sửa Đơn vị tính (Units) hoặc Danh mục (Categories)
                        .requestMatchers("/units/**", "/categories/**").hasAnyRole("ADMIN", "MANAGER")

                        // 5. Nghiệp vụ vận hành kho (ADMIN, MANAGER và STAFF)
                        .requestMatchers("/products/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers("/manager/inbound/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers("/manager/transfer/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers("/scan-station/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers("/scan-station/warehouses/**").authenticated()

                        // 6. Quyền cơ bản cho USER (Khách hàng/Khác)
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login") // TRÙNG VỚI th:action TRONG HTML
                        .successHandler((request, response, authentication) -> {
                            var roles = org.springframework.security.core.authority.AuthorityUtils
                                    .authorityListToSet(authentication.getAuthorities());

                            // 2. XÓA SẠCH REQUEST CŨ TRONG CACHE (Rất quan trọng)
                            new org.springframework.security.web.savedrequest.HttpSessionRequestCache()
                                    .removeRequest(request, response);

                            String targetUrl = "/";
                            if (roles.contains("ROLE_ADMIN")) {
                                targetUrl = "/admin/users";
                            } else if (roles.contains("ROLE_MANAGER")) {
                                targetUrl = "/manager/approvals"; // Quản lý vào trang duyệt đơn
                            } else if (roles.contains("ROLE_STAFF")) {
                                targetUrl = "/products"; // Nhân viên vào trang hàng hóa
                            }
                            response.sendRedirect(request.getContextPath() + targetUrl);
                        })
                        .failureUrl("/auth/login?error=true") // Thêm dòng này để báo lỗi nếu sai pass
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Đường dẫn để trigger logout
                        .logoutSuccessUrl("/auth/login?logout") // Đường dẫn sau khi logout thành công
                        .invalidateHttpSession(true) // Xóa session
                        .clearAuthentication(true) // Xóa xác thực
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Mã hóa mật khẩu cho bảng users [cite: 163]
    }
}
