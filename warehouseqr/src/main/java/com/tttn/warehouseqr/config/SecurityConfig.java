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
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/admin/users/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/products/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers("/categories/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
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
                            if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_MANAGER")) {
                                targetUrl = "/admin/users";
                            } else if (roles.contains("ROLE_STAFF")) {
                                targetUrl = "/products";
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
