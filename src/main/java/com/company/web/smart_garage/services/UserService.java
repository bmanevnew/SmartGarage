package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.user.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    void create(User user);
}
