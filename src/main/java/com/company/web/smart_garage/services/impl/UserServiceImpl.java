package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.exceptions.EntityDuplicationException;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.exceptions.RoleConflictException;
import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.repositories.UserRepository;
import com.company.web.smart_garage.services.RoleService;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.utils.PasswordUtility;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import static com.company.web.smart_garage.utils.AuthorizationUtils.userIsAdmin;
import static com.company.web.smart_garage.utils.AuthorizationUtils.userIsEmployee;
import static com.company.web.smart_garage.utils.Constants.*;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final EmailSenderServiceImpl senderService;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User getById(long id) {
        validateId(id);
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findFirstByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User", "username", username));
    }

    @Override
    public User getByEmail(String email) {
        validateEmail(email);
        return userRepository.findFirstByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User", "email", email));
    }

    @Override
    public User getByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findFirstByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new EntityNotFoundException("User", "username or email", usernameOrEmail));
    }

    public void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new InvalidParamException(USER_EMAIL_INVALID);
        }

        String[] emailParts = email.split("@");
        if (emailParts.length != 2) {
            throw new InvalidParamException(USER_EMAIL_INVALID);
        }

        String[] domainParts = emailParts[1].split("\\.");
        if (domainParts.length < 2) {
            throw new InvalidParamException(USER_EMAIL_INVALID);
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new InvalidParamException(USER_EMAIL_INVALID);
        }
    }

    @Override
    public User getByPhoneNumber(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        return userRepository.findFirstByPhoneNumber(phoneNumber).orElseThrow(
                () -> new EntityNotFoundException("User", "phone number", phoneNumber));
    }

    public void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.matches("^\\d{10}$")) {
            throw new InvalidParamException(USER_PHONE_INVALID);
        }
    }

    //TODO Cloudinary
    @Override
    public Page<User> getFilteredUsers(String name, String vehicleModel,
                                       String vehicleMake, String visitFromDate, String visitToDate,
                                       Pageable pageable) {
        LocalDateTime parsedFromDate = visitFromDate != null ? LocalDateTime.parse(visitFromDate + "T00:00:00") : null;
        LocalDateTime parsedToDate = visitToDate != null ? LocalDateTime.parse(visitToDate + "T00:00:00") : null;

        validateDateInterval(parsedFromDate, parsedToDate);
        validateSortProperties(pageable.getSort());

        Page<User> users = userRepository.findByFilters(name, vehicleModel,
                vehicleMake, parsedFromDate, parsedToDate, pageable);
        if (pageable.getPageNumber() >= users.getTotalPages()) {
            throw new InvalidParamException(PAGE_IS_INVALID);
        }
        return users;
    }

    private void validateSortProperties(Sort sort) {
        sort.get().forEach(order -> validateSortingProperty(order.getProperty()));
    }

    private void validateSortingProperty(String property) {
        switch (property) {
            case "username", "email", "phoneNumber", "firstName", "lastName" -> {
            }
            default -> throw new InvalidParamException(String.format(SORT_PROPERTY_S_IS_INVALID, property));
        }
    }

    @Override
    public User create(User user) {
        if (userRepository.findFirstByEmail(user.getEmail()).isPresent()) {
            throw new EntityDuplicationException(String.format(USER_WITH_EMAIL_S_ALREADY_EXISTS, user.getEmail()));
        }
        if (userRepository.findFirstByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new EntityDuplicationException(String.format(USER_WITH_PHONE_NUMBER_S_ALREADY_EXISTS, user.getPhoneNumber()));
        }
        //TODO implement random username in a better way
        String randomUsername;
        do {
            randomUsername = RandomStringUtils.randomAlphabetic(20);
        } while (userRepository.existsByUsername(randomUsername));
        user.setUsername(randomUsername);

        String originalPassword = PasswordUtility.generatePassword();
        user.setPassword(originalPassword);
        sendMail(user);
        String hash = passwordEncoder.encode(originalPassword);
        user.setPassword(hash);

        return userRepository.save(user);
    }

    @Override
    public User delete(long id) {
        User deletedUser = userRepository.findByIdFetchAll(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        userRepository.deleteById(id);
        return deletedUser;
    }

    @Override
    public void makeAdmin(long id) {
        User userToAdmin = getById(id);
        if (userIsAdmin(userToAdmin)) {
            throw new RoleConflictException(USER_IS_ALREADY_ADMIN);
        }

        Set<Role> roles = userToAdmin.getRoles();
        Role role = roleService.getByName("ROLE_ADMIN");
        roles.add(role);
        userToAdmin.setRoles(roles);

        userRepository.save(userToAdmin);
    }

    @Override
    public void makeEmployee(long id) {
        User userToEmployed = getById(id);
        if (userIsEmployee(userToEmployed)) {
            throw new RoleConflictException(USER_IS_ALREADY_EMPLOYEE);
        }

        Set<Role> roles = userToEmployed.getRoles();
        Role role = roleService.getByName("ROLE_EMPLOYEE");
        roles.add(role);
        userToEmployed.setRoles(roles);

        userRepository.save(userToEmployed);
    }

    @Override
    public void makeNotAdmin(long id) {
        User userToNotAdmin = getById(id);

        Set<Role> roles = userToNotAdmin.getRoles();
        Role adminRole = roles.stream()
                .filter(role -> role.getName().equals("ROLE_ADMIN"))
                .findFirst()
                .orElseThrow(() -> new RoleConflictException(USER_IS_NOT_ADMIN));

        roles.remove(adminRole);

        userToNotAdmin.setRoles(roles);
        userRepository.save(userToNotAdmin);
    }

    @Override
    public void makeUnemployed(long id) {
        User userToUnemployed = getById(id);

        Set<Role> roles = userToUnemployed.getRoles();
        Role employee = roles.stream()
                .filter(role -> "ROLE_EMPLOYEE".equals(role.getName()))
                .findFirst()
                .orElseThrow(() -> new RoleConflictException(USER_IS_NOT_EMPLOYED));

        roles.remove(employee);

        userToUnemployed.setRoles(roles);
        userRepository.save(userToUnemployed);
    }

    @Override
    public User update(User user) {
        User persistentUser = getById(user.getId());
        //changed properties
        persistentUser.setEmail(user.getEmail());
        persistentUser.setPhoneNumber(user.getPhoneNumber());
        persistentUser.setFirstName(user.getFirstName());
        persistentUser.setLastName(user.getLastName());
        if (user.getPassword() != null) {
            if (PasswordUtility.validatePassword(user.getPassword())) {
                String hash = passwordEncoder.encode(user.getPassword());
                persistentUser.setPassword(hash);
            } else {
                throw new InvalidParamException(PASSWORD_TOO_WEAK);
            }
        }

        //throws exception if email duplication occurs
        boolean duplicateEmailExists = true;
        try {
            User existingUser = getByEmail(persistentUser.getEmail());
            if (Objects.equals(existingUser.getId(), persistentUser.getId())) {
                duplicateEmailExists = false;
            }
        } catch (EntityNotFoundException e) {
            duplicateEmailExists = false;
        }
        if (duplicateEmailExists) throw new EntityDuplicationException(
                String.format(USER_WITH_EMAIL_S_ALREADY_EXISTS, persistentUser.getEmail()));

        //throws exception if phone number duplication occurs
        boolean duplicatePhoneNumberExists = true;
        try {
            User existingUser = getByPhoneNumber(persistentUser.getPhoneNumber());
            if (Objects.equals(existingUser.getId(), persistentUser.getId())) {
                duplicatePhoneNumberExists = false;
            }
        } catch (EntityNotFoundException e) {
            duplicatePhoneNumberExists = false;
        }
        if (duplicatePhoneNumberExists) throw new EntityDuplicationException(
                String.format(USER_WITH_PHONE_NUMBER_S_ALREADY_EXISTS, persistentUser.getPhoneNumber()));

        return userRepository.save(persistentUser);
    }

    private boolean validateDate(LocalDateTime date) {
        if (date != null) {
            if (date.isAfter(LocalDateTime.now())) {
                throw new InvalidParamException(VISIT_DATE_IN_FUTURE);
            }
            return true;
        }
        return false;
    }

    private void validateDateInterval(LocalDateTime dateFrom, LocalDateTime dateTo) {
        if (validateDate(dateFrom) & validateDate(dateTo)) {
            if (dateFrom.isAfter(dateTo)) {
                throw new InvalidParamException(VISIT_DATE_INTERVAL_INVALID);
            }
        }
    }
    private void validateId(Long id) {
        if (id != null && id <= 0) {
            throw new InvalidParamException(ID_MUST_BE_POSITIVE);
        }
    }

    public void sendMail(User user) {
        String toEmail = user.getEmail();
        String subject = "Welcome to Smart Garage!";
        String body = "Dear " + user.getUsername() + ",\n\n" +
                "Welcome to Smart Garage! Here are your login details:\n\n" +
                "Username: " + user.getUsername() + "\n" +
                "Password: " + user.getPassword() + "\n\n" +
                "Best regards,\n" +
                "The Smart Garage Team";
        senderService.sendEmail(toEmail,
                subject, body);
    }
}
