package com.company.web.smart_garage.utils.mappers;

import com.company.web.smart_garage.data_transfer_objects.UserDtoIn;
import com.company.web.smart_garage.data_transfer_objects.UserDtoOut;
import com.company.web.smart_garage.models.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public User dtoToUser(UserDtoIn dto) {
        return modelMapper.map(dto, User.class);
    }

    public User dtoToUser(UserDtoIn dto, long id) {
        User user = dtoToUser(dto);
        user.setId(id);
        return user;
    }

    public UserDtoOut userToDto(User user) {
        return modelMapper.map(user, UserDtoOut.class);
    }
}
