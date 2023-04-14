package com.company.web.smart_garage.utils.helpers;

import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.models.user.User;
import com.company.web.smart_garage.models.user.UserDtoIn;
import com.company.web.smart_garage.models.user.UserDtoOut;
import com.company.web.smart_garage.models.user.UserDtoOutSimple;
import com.company.web.smart_garage.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
@Component
public class UserMapper {
    private final RoleService roleService;
    private final VehicleMapper vehicleMapper;
    private final VisitMapper visitMapper;

    public UserMapper(RoleService roleService, VehicleMapper vehicleMapper, @Lazy VisitMapper visitMapper) {
        this.roleService = roleService;
        this.vehicleMapper = vehicleMapper;
        this.visitMapper = visitMapper;
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

    public User dtoToObject(UserDtoOut userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        if (userDto.getRoles()==null){
            Role role = roleService.getById(1);
            user.setRoles(Collections.singleton(role));
        }else {
            user.setRoles(userDto.getRoles());
        }
        return user;
    }

    public UserDtoOut objectToDto(User user) {
        UserDtoOut userDto = new UserDtoOut();
        userDto.setId(Optional.ofNullable(user.getId()).orElse(0L));
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        userDto.setPhoneNumber(user.getPhoneNumber());
        if (user.getVisits() != null) {
            userDto.setVisits(user.getVisits().stream().map(visitMapper::visitDtoOutSimple).collect(Collectors.toSet()));
        }
        // TODO make a simpleVisitsDTo
        if (user.getVehicles() != null) {
            userDto.setVehicles(user.getVehicles().stream().map(vehicleMapper::vehicleToDto).collect(Collectors.toSet()));
        }

//        if (!user.getRoles().isEmpty()) {
//            userDto.setRoles(getRoleNames(user.getRoles()));
//        }
//        if (user.getRoles().isEmpty()) {
//            userDto.setRoles(Collections.singletonList(repository.getById(1).getRoleName()));
//        }

        return userDto;
    }

    public UserDtoOutSimple ObjectToDtoSimple(User user) {
        UserDtoOutSimple userDto = new UserDtoOutSimple();
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPhoneNumber(user.getPhoneNumber());
        return userDto;
    }

}
