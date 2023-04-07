package com.company.web.smart_garage.utils.helpers;

import com.company.web.smart_garage.models.user.User;
import com.company.web.smart_garage.models.user.UserDtoIn;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User dtoToUser(UserDtoIn dto){
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        return user;
    }
}
