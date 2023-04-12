package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.exceptions.UnauthorizedOperationException;
import com.company.web.smart_garage.models.PasswordGenerator;
import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.models.user.User;
import com.company.web.smart_garage.repositories.RoleRepository;
import com.company.web.smart_garage.repositories.UserRepository;
import com.company.web.smart_garage.services.RoleService;
import com.company.web.smart_garage.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.company.web.smart_garage.utils.AuthorizationUtils.*;
import static com.company.web.smart_garage.utils.Constants.*;

@Service

public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final RoleService roleService;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
    }


    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    public User getByUsername(String username) {
        throwIfUsernameIsDeleted(username);
        return userRepository.findFirstByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User", "username", username));
    }

    @Override
    public User getByEmail(String email) {
        validateEmail(email);
        return userRepository.findFirstByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User", "email", email));
    }
    private void validateEmail(String email) {
        if (email==null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new InvalidParamException(USER_EMAIL_INVALID);
        }
    }
    private void validatePhoneNumber(String phoneNumber) {
        int as = phoneNumber.length();
        if (as!=10 ) {
            throw new InvalidParamException(USER_PHONE_INVALID);
        }
    }
    @Override
    public User getByPhoneNumber(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        return userRepository.findFirstByPhoneNumber(phoneNumber).orElseThrow(
                () -> new EntityNotFoundException("User", "phone number", phoneNumber));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
    //TODO Cloudinary
    @Override
    public Page<User> getFilteredUsers(String name, String vehicleModel,
                                       String vehicleMake, String visitFromDate, String visitToDate,
                                       Pageable pageable) {
        LocalDate parsedFromDate = visitFromDate != null ? LocalDate.parse(visitFromDate) : null;
        LocalDate parsedToDate = visitToDate != null ? LocalDate.parse(visitToDate) : null;


        Page<User> users = userRepository.findByFilters(name, vehicleModel,
                vehicleMake, parsedFromDate, parsedToDate, pageable);
        if (pageable.getPageNumber() >= users.getTotalPages()) {
            throw new InvalidParamException(PAGE_IS_INVALID);
        }
        return users;
    }

    private void validateSortProperties(String sortBy, String sortOrder) {
        if (sortBy != null && !Arrays.asList("name", "visitDate").contains(sortBy)) {
            throw new InvalidParamException("Invalid sort property.");
        }

        if (sortOrder != null && !Arrays.asList("asc", "desc").contains(sortOrder)) {
            throw new InvalidParamException("Invalid sort order.");
        }
    }

    private void validateSortingProperty(String property) {
        switch (property) {
            case "username", "email", "phoneNumber", "model", "brand", "date" -> {
            }
            default -> throw new InvalidParamException(String.format(SORT_PROPERTY_S_IS_INVALID, property));
        }
    }
    @Override
    public User create(User user) {
        user.setPassword(PasswordGenerator.generatePassword());
        return userRepository.save(user);
    }

    @Override
    public void makeAdmin(int id, User userPerformingAction) {
        User userToAdmin = getUserById(id);
        checkModifyPermissions(userPerformingAction);
        if (userIsDeleted(userToAdmin)) {
            throw new UnsupportedOperationException(USER_IS_ALREADY_DELETED);
        }

        Set<Role> roles = userToAdmin.getRoles();
        Role role = roleRepository.findByName("admin");
        roles.add(role);

        userToAdmin.setRoles(roles);
        userRepository.save(userToAdmin);
    }

    @Override
    public void makeEmployee(int id, User userPerformingAction) {
        User userToEmployed = getUserById(id);
        checkModifyPermissions(userPerformingAction);
        if (userIsDeleted(userToEmployed)) {
            throw new UnsupportedOperationException(USER_IS_ALREADY_DELETED);
        }

        Set<Role> roles = userToEmployed.getRoles();
        Role role = roleRepository.findByName("employee");
        roles.add(role);

        userToEmployed.setRoles(roles);
        userRepository.save(userToEmployed);
    }

    @Override
    public void makeNotAdmin(int id, User userPerformingAction) {
        User userToNotAdmin = getUserById(id);
        checkModifyPermissions(userPerformingAction);
        if (userIsDeleted(userToNotAdmin)) {
            throw new UnsupportedOperationException(USER_IS_ALREADY_DELETED);
        }

        Set<Role> roles = userToNotAdmin.getRoles();
        Role employee = roles.stream()
                .filter(role -> "admin".equals(role.getName()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(USER_IS_NOT_ADMIN));

        roles.remove(employee);

        userToNotAdmin.setRoles(roles);
        userRepository.save(userToNotAdmin);
    }

    @Override
    public void makeUnemployed(int id, User userPerformingAction) {
        User userToUnemployed = getUserById(id);
        checkModifyPermissions(userPerformingAction);
        if (userIsDeleted(userToUnemployed)) {
            throw new UnsupportedOperationException(USER_IS_ALREADY_DELETED);
        }

        Set<Role> roles = userToUnemployed.getRoles();
        Role employee = roles.stream()
                .filter(role -> "employee".equals(role.getName()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(USER_IS_NOT_EMPLOYED));

        roles.remove(employee);

        userToUnemployed.setRoles(roles);
        userRepository.save(userToUnemployed);
    }

    public List<User> getUsersByRole(Role role) {

        return userRepository.findByRolesIn(Collections.singletonList(role));
    }

    public void update(long id, User user, User requester) {
        if (userIsEmployee(requester) || userIsAdmin(requester) || requester.getId() == user.getId()) {
            user.setPassword(getUserById(id).getPassword());
            user.setEmail(getUserById(id).getEmail());
            user.setPassword(getUserById(id).getPassword());
            user.setRoles(getUserById(id).getRoles());
            user.setVehicles(getUserById(id).getVehicles());
            boolean duplicateExists = true;
            try {
                User existingUser = getByEmail(user.getEmail());
                if (existingUser.getId() == user.getId()) {
                    duplicateExists = false;
                }
            } catch (EntityNotFoundException e) {
                duplicateExists = false;
            }
            if (duplicateExists) throw new EntityNotFoundException("User", "username", user.getUsername());

            userRepository.save(user);
            return;
        }
        throw new UnauthorizedOperationException("You must log in with the user that you're trying to update.");
    }

    public void delete(long id, User userPerformingAction) {
        User userToBeDeleted = getUserById(id);
        checkModifyPermissions(userPerformingAction);

        if (userIsDeleted(userToBeDeleted)) {
            throw new UnsupportedOperationException(USER_IS_ALREADY_DELETED);
        }

        userToBeDeleted.setUsername(DELETED + userToBeDeleted.getId());
        Set<Role> roles = userToBeDeleted.getRoles();
        roles.add(roleService.getById(4));
        userToBeDeleted.setRoles(roles);
        userRepository.save(userToBeDeleted);

    }

//    private void checkModifyPermissions(User user, User userPerformingAction) {
//        if (userIsDeleted(userPerformingAction) || !userIsAdmin(userPerformingAction)
//                || !userIsEmployee(userPerformingAction)) {
//            throw new UnauthorizedOperation(DELETED_NOT_EMPLOYEES_OR_NOT_ADMINS_ERROR_MESSAGE);
//        }
//        if (!(userIsAdmin(userPerformingAction) || user.equals(userPerformingAction))) {
//            throw new UnauthorizedOperation(MODIFY_ENTITY_ERROR_MESSAGE);
//        }
//    }

    private void checkModifyPermissions(User userPerformingAction) {
        //TODO add a check is the userPerformingAction isAdmin
        if (userIsDeleted(userPerformingAction)) {
            throw new UnauthorizedOperationException(USER_IS_ALREADY_DELETED);
        } else if (!userIsEmployee(userPerformingAction)) {
            System.out.println(userIsEmployee(userPerformingAction));
            throw new UnauthorizedOperationException(NOT_EMPLOYEES_OR_ADMINS_ERROR_MESSAGE);
        }

    }

    private void throwIfUsernameIsDeleted(String username) {
        if (username.equalsIgnoreCase(DELETED)) {
            throw new UnsupportedOperationException(USERNAME_DELETED_IS_INVALID);
        }
    }

    private User throwIfUserDeleted(User user) {
        if (userIsDeleted(user)) {
            throw new UnauthorizedOperationException(USER_IS_ALREADY_DELETED);
        }
        return user;
    }
}
