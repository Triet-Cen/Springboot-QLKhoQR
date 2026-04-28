package com.tttn.warehouseqr.common.util;

import com.tttn.warehouseqr.modules.auth.entity.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SecurityUtils {
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }
        return null; // Hoặc ném ra một Exception nếu bắt buộc phải đăng nhập
    }

    public static boolean hasAnyRole(String... roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null || roles == null || roles.length == 0) {
            return false;
        }

        Set<String> authorities = auth.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());

        for (String role : roles) {
            if (role == null || role.isBlank()) {
                continue;
            }
            String normalized = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            if (authorities.contains(normalized)) {
                return true;
            }
        }
        return false;
    }
}