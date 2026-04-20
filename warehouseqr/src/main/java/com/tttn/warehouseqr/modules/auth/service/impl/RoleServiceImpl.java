package com.tttn.warehouseqr.modules.auth.service.impl;

import com.tttn.warehouseqr.common.exception.AppException;
import com.tttn.warehouseqr.common.exception.ErrorCode;
import com.tttn.warehouseqr.modules.auth.dto.UserCreateRequest;
import com.tttn.warehouseqr.modules.auth.dto.UserUpdateRequest;
import com.tttn.warehouseqr.modules.auth.entity.Role;
import com.tttn.warehouseqr.modules.auth.entity.User;
import com.tttn.warehouseqr.modules.auth.repository.RoleRepository;
import com.tttn.warehouseqr.modules.auth.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
