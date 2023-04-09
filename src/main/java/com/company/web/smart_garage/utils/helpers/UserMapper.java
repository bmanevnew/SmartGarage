package com.company.web.smart_garage.utils.helpers;

import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.models.user.User;
import com.company.web.smart_garage.models.user.UserDtoIn;
import com.company.web.smart_garage.models.user.UserDtoOut;
import com.company.web.smart_garage.services.RoleService;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class UserMapper {
    private final RoleService roleService;

    public UserMapper(RoleService roleService) {
        this.roleService = roleService;
    }


    public User dtoToUser(UserDtoIn dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        return user;
    }

    public User dtoToObject(UserDtoIn userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        Role role = roleService.getById(1);
        user.setRoles(Collections.singleton(role));
        return user;
    }

    public UserDtoOut ObjectToDto(User user) {
        UserDtoOut userDto = new UserDtoOut();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        userDto.setVehicles(user.getVehicles());
//        if (!user.getRoles().isEmpty()) {
//            userDto.setRoles(getRoleNames(user.getRoles()));
//        }
//        if (user.getRoles().isEmpty()) {
//            userDto.setRoles(Collections.singletonList(repository.getById(1).getRoleName()));
//        }

        return userDto;
    }

}
