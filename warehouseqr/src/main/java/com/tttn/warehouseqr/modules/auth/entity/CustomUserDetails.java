package com.tttn.warehouseqr.modules.auth.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final User user;
    private String avatar;

    public CustomUserDetails(User user) {
        this.user = user;
        this.avatar = (user.getAvatar() != null && !user.getAvatar().isEmpty())
                ? "/assets/images/avatars/" + user.getAvatar()
                : "/assets/images/default-avatar.png";
    }
    public Long getUserId() {
        return user.getUserId();
    }

    public String getFullName() {
        return user.getFullName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = user.getRole().getRoleName();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }
        return Collections.singleton(new SimpleGrantedAuthority(roleName));
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getUsername(); }

    // Các hàm này mặc định trả về true để không bị khóa tài khoản
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}