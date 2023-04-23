package com.company.web.smart_garage.utils.mappers;

import com.company.web.smart_garage.data_transfer_objects.ProfileUpdateDto;
import com.company.web.smart_garage.data_transfer_objects.UserDtoIn;
import com.company.web.smart_garage.data_transfer_objects.UserDtoOut;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public User dtoToUser(UserDtoIn dto) {
        return modelMapper.map(dto, User.class);
    }

    public User dtoToUser(UserDtoIn dto, long id) {
        User user = dtoToUser(dto);
        user.setId(id);
        return user;
    }

    public User profileDtoToUser(ProfileUpdateDto dto) {
        return modelMapper.map(dto, User.class);
    }


    public User profileDtoToUser(ProfileUpdateDto dto, long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Visit", id));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }


        return user;
    }

    public UserDtoOut userToDto(User user) {
        return modelMapper.map(user, UserDtoOut.class);
    }

    public ProfileUpdateDto userToDtoUpdate(User user) {
        return modelMapper.map(user, ProfileUpdateDto.class);
    }
}
