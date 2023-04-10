package com.company.web.smart_garage.controllers;

import com.company.web.smart_garage.exceptions.EntityDuplicationException;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.models.user.User;
import com.company.web.smart_garage.models.user.UserDtoIn;
import com.company.web.smart_garage.models.user.UserDtoOut;
import com.company.web.smart_garage.services.RoleService;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.utils.helpers.AuthenticationHelper;
import com.company.web.smart_garage.utils.helpers.UserMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final AuthenticationHelper authenticationHelper;

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody UserDtoIn userDto) {
        User user = userMapper.dtoToUser(userDto);
        Role role = roleService.getById(1);
        user.setRoles(Collections.singleton(role));
        return userService.create(user);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable long id) {
        try {
            return userService.getUserById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
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
    public User update(@RequestHeader HttpHeaders headers, @PathVariable int id,
                       @Valid @RequestBody UserDtoIn userDtoIn) {
        try {
            User requester = authenticationHelper.tryGetUser(headers);
            User updatedUser = userMapper.dtoToUser(userDtoIn);
            updatedUser.setId(id);
            userService.update(id, updatedUser, requester);
            updatedUser = getUserById(id);
            return updatedUser;
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
            User deleteUser = getUserById(id);
            userService.delete(id, user);
            return userMapper.ObjectToDto(deleteUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    private User getUserById(long id) {
        try {
            return userService.getUserById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/roles")
    public Set<Role> getRoles(@PathVariable int id) {
        return getUserById(id).getRoles();
    }

    @GetMapping("/{id}/roles/{roleId}")
    public Role getRole(@PathVariable int id) {
        return getUserById(id).getRoles()
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
            User userToBeEmployed = getUserById(id);
            return userMapper.ObjectToDto(userToBeEmployed);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}/makeAdmin")
    public UserDtoOut makeAdmin(@RequestHeader HttpHeaders headers,
                                @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.makeAdmin(id, user);
            User userToBeAdmin = getUserById(id);
            return userMapper.ObjectToDto(userToBeAdmin);
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
            User userToBeEmployed = getUserById(id);
            return userMapper.ObjectToDto(userToBeEmployed);
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
            User userToBeAdmin = getUserById(id);
            return userMapper.ObjectToDto(userToBeAdmin);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
