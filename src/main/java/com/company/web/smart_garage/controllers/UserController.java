package com.company.web.smart_garage.controllers;

import com.company.web.smart_garage.helpers.UserMapper;
import com.company.web.smart_garage.models.user.User;
import com.company.web.smart_garage.models.user.UserDtoIn;
import com.company.web.smart_garage.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody UserDtoIn userDto) {
        User user = userMapper.dtoToUser(userDto);
        userService.create(user);
        return user;
    }
}
