package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.user.User;

import java.util.List;

public interface UserService {
    User getUserById(long id);

    User getByUsername(String username);

    User getByEmail(String email);

    User getByPhoneNumber(String phoneNumber);

    List<User> getAll();

    User create(User user);

    void update(long id, User user, User requester);

    void delete(long id, User user);

    void makeAdmin(int id, User user);

    void makeEmployee(int id, User user);

    void makeNotAdmin(int id, User user);

    void makeUnemployed(int id, User user);
}
