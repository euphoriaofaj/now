package com.morago.backend.service.role;

import com.morago.backend.entity.Role;
import com.morago.backend.entity.enumFiles.Roles;
import com.morago.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;

    @Override
    public Optional<Role> findByName(Roles name) {
        return roleRepository.findByName(name);
    }

    public Role getRoleOrThrow(Roles name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Role not configured in DB: " + name));
    }
}
