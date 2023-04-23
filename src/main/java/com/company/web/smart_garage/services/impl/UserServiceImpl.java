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
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Override
    public Page<User> getFilteredUsers(String name, String vehicleModel,
                                       String vehicleMake, LocalDate dateFrom, LocalDate dateTo,
                                       Pageable pageable) {

//        LocalDateTime parsedFromDate = visitFromDate != null ? LocalDate.parse(visitFromDate).atStartOfDay() :
//                LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
//        LocalDateTime parsedToDate = visitToDate != null ? LocalDate.parse(visitToDate).atStartOfDay() :
//                LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        validateDateInterval(dateFrom, dateTo);
        validateSortProperties(pageable.getSort());

        if (name != null && name.isBlank()) name = null;
        if (vehicleMake != null && vehicleMake.isBlank()) vehicleMake = null;
        if (vehicleModel != null && vehicleModel.isBlank()) vehicleModel = null;

        Page<User> users = userRepository.findByFilters(name, vehicleModel, vehicleMake,
                (dateFrom == null ? null : java.sql.Date.valueOf(dateFrom)),
                (dateTo == null ? null : java.sql.Date.valueOf(dateTo)),
                pageable);


        if (pageable.getPageNumber() >= users.getTotalPages() && pageable.getPageNumber() != 0) {
            throw new InvalidParamException(PAGE_IS_INVALID);
        }
        return users;
    }

    private void validateSortProperties(Sort sort) {
        sort.get().forEach(order -> validateSortingProperty(order.getProperty()));
    }

    private void validateSortingProperty(String property) {
        switch (property) {
            case "username", "email", "phoneNumber", "firstName", "lastName", "dateFrom", "model", "brand" -> {
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

        String randomUsername;
        do {
            randomUsername = generateRandomUsername();
        } while (randomUsername.length() > 20 || userRepository.existsByUsername(randomUsername));
        user.setUsername(randomUsername);

        String originalPassword = PasswordUtility.generatePassword();
        user.setPassword(originalPassword);
        sendMail(user);
        String hash = passwordEncoder.encode(originalPassword);
        user.setPassword(hash);

        user.setRoles(Set.of(roleService.getByName("ROLE_CUSTOMER")));

        return userRepository.save(user);
    }

    private String generateRandomUsername() {
        Faker faker = new Faker();
        return (WordUtils.capitalizeFully(faker.color().name()) +
                WordUtils.capitalizeFully(faker.animal().name()))
                .replace(" ", "") + faker.number().digits(2);
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

        //TODO Add addRole method
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
        if (userRepository.findFirstByPhoneNumber(user.getPhoneNumber()).isPresent() && !Objects.equals(user.getId(), persistentUser.getId())) {
            throw new EntityDuplicationException(String.format(USER_WITH_PHONE_NUMBER_S_ALREADY_EXISTS, user.getPhoneNumber()));
        }
        if (user.getFirstName().length() < 4 || user.getLastName().length() < 4) {
            throw new InvalidParamException("First name and last name should be at least 4 characters long.");
        }
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

    private boolean validateDate(LocalDate date) {
        if (date != null) {
            if (date.isAfter(LocalDate.now())) {
                throw new InvalidParamException(VISIT_DATE_IN_FUTURE);
            }
            return true;
        }
        return false;
    }

    private void validateDateInterval(LocalDate dateFrom, LocalDate dateTo) {
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
