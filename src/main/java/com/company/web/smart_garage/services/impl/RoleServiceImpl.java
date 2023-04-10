package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.repositories.RoleRepository;
import com.company.web.smart_garage.services.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    public RoleRepository roleRepository;

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public Role getById(long id) {
        return roleRepository.
                findById(id).
                orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    public Role getRoleByName(String name) {
        Optional<Role> roleOptional = Optional.ofNullable(roleRepository.findByName(name));
        return roleOptional.orElseGet(() -> roleRepository.findByName(name));
    }
}
