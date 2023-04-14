package com.company.web.smart_garage.controllers;

import com.company.web.smart_garage.data_transfer_objects.UserDtoIn;
import com.company.web.smart_garage.data_transfer_objects.UserDtoOut;
import com.company.web.smart_garage.exceptions.EntityDuplicationException;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.services.RoleService;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.utils.helpers.AuthenticationHelper;
import com.company.web.smart_garage.utils.helpers.UserMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final AuthenticationHelper authenticationHelper;


    @GetMapping("/{id}")
    public User getById(@PathVariable long id) {
        try {
            return userService.getUserById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping(params = "email")
    public UserDtoOut getByEmail(@RequestParam(name = "email") String email) {
        return userMapper.objectToDto(userService.getByEmail(email));
    }

    @GetMapping(params = "phone-number")
    public UserDtoOut getByPhoneNumber(@RequestParam(name = "phone-number") String phoneNumber) {
        return userMapper.objectToDto(userService.getByPhoneNumber(phoneNumber));
    }

    @GetMapping
    public List<UserDtoOut> getAll(@RequestParam(required = false, name = "name") String name,
                                   @RequestParam(required = false, name = "vehicle-model") String vehicleModel,
                                   @RequestParam(required = false, name = "vehicle-brand") String vehicleMake,
                                   @RequestParam(required = false, name = "visit-from-date") String visitFromDate,
                                   @RequestParam(required = false, name = "visit-to-date") String visitToDate,
                                   Pageable pageable) {
//TODO visitDate searching to be fixed
        Page<User> users = userService.getFilteredUsers(name, vehicleModel, vehicleMake,
                visitFromDate, visitToDate, pageable);
        return users.getContent().stream().map(userMapper::objectToDto).collect(Collectors.toList());
    }

    @PostMapping
    public User create(@Valid @RequestBody UserDtoIn userDto) {
        User user = userMapper.dtoToUser(userDto);
        Role role = roleService.getById(1);
        user.setRoles(Collections.singleton(role));
        return userService.create(user);
    }

//    @PostMapping
//    public UserDtoOut createUser(@RequestHeader HttpHeaders headers, @Valid @RequestBody UserDtoIn userDto) {
//
//            User user = userMapper.dtoToObject(userDto);
//            userService.create(user);
//            return userMapper.ObjectToDto(user);

    //        catch (EntityDuplicationException | UnsupportedOperationException e) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
//        }
    //  }
    @PutMapping("/{id}")
    public UserDtoOut update(@RequestHeader HttpHeaders headers, @PathVariable long id,
                       @Valid @RequestBody UserDtoIn userDtoIn) {
        try {
            User requester = authenticationHelper.tryGetUser(headers);
            User updatedUser = userMapper.dtoToUser(userDtoIn);
            updatedUser.setId(id);
            userService.update(id, updatedUser, requester);
            updatedUser = userMapper.dtoToObject(getById(id));
            return userMapper.objectToDto(updatedUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityDuplicationException | UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public UserDtoOut delete(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            User deleteUser = userMapper.dtoToObject(getById(id));
            userService.delete(id, user);
            return userMapper.objectToDto(deleteUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

//    private User getUserById(long id) {
//        try {
//            return userService.getUserById(id);
//        } catch (EntityNotFoundException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        }
//    }

    @GetMapping("/{id}/roles")
    public Set<Role> getRoles(@PathVariable int id) {
        return getById(id).getRoles();
    }

    @GetMapping("/{id}/roles/{roleId}")
    public Role getRole(@PathVariable int id) {
        return getById(id).getRoles()
                .stream().filter(r -> r.getId() == id)
                .findFirst().orElse(null);
    }
    //--------------------------------ROLES-------------------------------------

    @PutMapping("/{id}/makeEmployee")
    public UserDtoOut makeEmployee(@RequestHeader HttpHeaders headers,
                                   @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.makeEmployee(id, user);
            User userToBeEmployed = userMapper.dtoToObject(getById(id));
            return userMapper.objectToDto(userToBeEmployed);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}/makeAdmin")
    public UserDtoOut makeAdmin(@RequestHeader HttpHeaders headers,
                                @PathVariable long id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.makeAdmin(id, user);
            User userToBeAdmin = userMapper.dtoToObject(getById(id));
            return userMapper.objectToDto(userToBeAdmin);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}/makeUnemployed")
    public UserDtoOut makeUnemployed(@RequestHeader HttpHeaders headers,
                                     @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.makeUnemployed(id, user);
            User userToBeEmployed = userMapper.dtoToObject(getById(id));
            return userMapper.objectToDto(userToBeEmployed);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}/makeNotAdmin")
    public UserDtoOut makeNotAdmin(@RequestHeader HttpHeaders headers,
                                   @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.makeNotAdmin(id, user);
            User userToBeAdmin = userMapper.dtoToObject(getById(id));
            return userMapper.objectToDto(userToBeAdmin);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
