package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.Role;

import java.util.List;

public interface RoleService {
    List<Role> getAll();

    Role getById(long id);
}
