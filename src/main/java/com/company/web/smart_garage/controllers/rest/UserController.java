package com.company.web.smart_garage.controllers.rest;

import com.company.web.smart_garage.data_transfer_objects.UserDtoIn;
import com.company.web.smart_garage.data_transfer_objects.UserDtoOut;
import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.utils.mappers.UserMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static com.company.web.smart_garage.utils.AuthorizationUtils.userIsAdmin;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public UserDtoOut getByIdDto(@PathVariable long id) {
        return userMapper.userToDto(getById(id));
    }

    private User getById(long id) {
        return userService.getById(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping(params = "username")
    public UserDtoOut getByUsername(@RequestParam(name = "username") String username) {
        return userMapper.userToDto(userService.getByUsername(username));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping(params = "email")
    public UserDtoOut getByEmail(@RequestParam(name = "email") String email) {
        return userMapper.userToDto(userService.getByEmail(email));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping(params = {"usernameOrEmail"})
    public UserDtoOut getByUsernameOrEmail(@RequestParam(name = "usernameOrEmail") String usernameOrEmail) {
        return userMapper.userToDto(userService.getByUsernameOrEmail(usernameOrEmail));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping(params = "phone-number")
    public UserDtoOut getByPhoneNumber(@RequestParam(name = "phone-number") String phoneNumber) {
        return userMapper.userToDto(userService.getByPhoneNumber(phoneNumber));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping
    public List<UserDtoOut> getAll(@RequestParam(required = false, name = "name") String name,
                                   @RequestParam(required = false, name = "vehicle-model") String vehicleModel,
                                   @RequestParam(required = false, name = "vehicle-brand") String vehicleMake,
                                   @RequestParam(required = false, name = "visit-from-date") String visitFromDate,
                                   @RequestParam(required = false, name = "visit-to-date") String visitToDate,
                                   Pageable pageable) {

        return userService.getFilteredUsers(name, vehicleModel, vehicleMake, visitFromDate, visitToDate, pageable)
                .map(userMapper::userToDto).toList();
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PostMapping
    public UserDtoOut create(@Valid @RequestBody UserDtoIn userDto) {
        User user = userMapper.dtoToUser(userDto);
        return userMapper.userToDto(userService.create(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDtoOut> update(@PathVariable long id,
                                             @Valid @RequestBody UserDtoIn userDtoIn,
                                             Authentication authentication) {
        //regular customer can only update his own account
        if (!userIsAdmin(authentication) &&
                !(id == (userService.getByUsernameOrEmail(authentication.getName()).getId()))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        User updatedUser = userMapper.dtoToUser(userDtoIn, id);
        return ResponseEntity.ok(userMapper.userToDto(userService.update(updatedUser)));
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public UserDtoOut delete(@PathVariable int id) {
        return userMapper.userToDto(userService.delete(id));
    }

    //--------------------------------ROLES-------------------------------------

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}/roles")
    public Set<Role> getRoles(@PathVariable int id) {
        return getById(id).getRoles();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/makeEmployee")
    public UserDtoOut makeEmployee(@PathVariable int id) {
        User user = getById(id);
        userService.makeEmployee(id);
        return userMapper.userToDto(user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/makeAdmin")
    public UserDtoOut makeAdmin(@PathVariable long id) {
        User user = getById(id);
        userService.makeAdmin(id);
        return userMapper.userToDto(user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/makeUnemployed")
    public UserDtoOut makeUnemployed(@PathVariable int id) {
        User user = getById(id);
        userService.makeUnemployed(id);
        return userMapper.userToDto(user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/makeNotAdmin")
    public UserDtoOut makeNotAdmin(@PathVariable int id) {
        User user = getById(id);
        userService.makeNotAdmin(id);
        return userMapper.userToDto(user);
    }
}
