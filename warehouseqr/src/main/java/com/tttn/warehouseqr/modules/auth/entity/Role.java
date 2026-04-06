package com.tttn.warehouseqr.modules.auth.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(nullable = false, length = 100, unique = true)
    private String roleName;

    @OneToMany(mappedBy = "role")
    private List<User> users;

    public Role() {
    }

    public Role(String roleName) {
        this.roleName = roleName;
    }

    // --- GETTER & SETTER ---
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
}