package com.morago.backend.service.role;

import com.morago.backend.entity.Role;
import com.morago.backend.entity.enumFiles.Roles;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(Roles name);
    Role getRoleOrThrow(Roles name);
}
