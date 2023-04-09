package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.user.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User create(User user);

    User getUserById(long id);

    void update(long id, User user, User requester);

    void delete(long id, User user);

    public User getByUsername(String username);

    void makeAdmin(int id, User user);

    void makeEmployee(int id, User user);

    void makeNotAdmin(int id, User user);

    void makeUnemployed(int id, User user);
}
