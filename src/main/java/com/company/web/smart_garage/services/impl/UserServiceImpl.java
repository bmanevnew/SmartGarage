package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.models.user.User;
import com.company.web.smart_garage.repositories.UserRepository;
import com.company.web.smart_garage.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }
}
