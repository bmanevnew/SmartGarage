package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.repositories.RoleRepository;
import com.company.web.smart_garage.services.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    public RoleRepository roleRepository;

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public Role getById(long id) {
        return roleRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Role", id));
    }

    public Role getByName(String name) {
        return roleRepository.findFirstByName(name).orElseThrow(() -> new EntityNotFoundException("Role", "name", name));
    }
}
