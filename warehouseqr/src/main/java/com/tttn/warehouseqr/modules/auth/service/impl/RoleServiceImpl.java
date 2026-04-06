package com.tttn.warehouseqr.modules.auth.service.impl;

import com.tttn.warehouseqr.modules.auth.entity.Role;
import com.tttn.warehouseqr.modules.auth.repository.RoleRepository;
import com.tttn.warehouseqr.modules.auth.service.RoleService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}