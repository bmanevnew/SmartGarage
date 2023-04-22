package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserService {
    User getById(long id);

    User getByUsername(String username);

    User getByEmail(String email);

    User getByUsernameOrEmail(String usernameOrEmail);

    User getByPhoneNumber(String phoneNumber);

    Page<User> getFilteredUsers(String name, String vehicleModel, String vehicleMake, LocalDate visitFromDate,
                                LocalDate visitToDate, Pageable pageable);

    User create(User user);

    User update(User user);

    User delete(long id);

    void makeAdmin(long id);

    void makeEmployee(long id);

    void makeNotAdmin(long id);

    void makeUnemployed(long id);
}
