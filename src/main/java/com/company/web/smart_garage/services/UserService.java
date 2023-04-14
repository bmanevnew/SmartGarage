package com.company.web.smart_garage.services;

import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

import static com.company.web.smart_garage.utils.Constants.PAGE_IS_INVALID;

public interface UserService {
    User getUserById(long id);

    User getByUsername(String username);

    User getByEmail(String email);

    User getByPhoneNumber(String phoneNumber);

    List<User> getAll();

    public Page<User> getFilteredUsers( String name,
                                        String vehicleModel,
                                        String vehicleMake, String visitFromDate,
                                        String visitToDate, Pageable pageable) ;



    User create(User user);

    void update(long id, User user, User requester);

    void delete(long id, User user);

    void makeAdmin(long id, User user);

    void makeEmployee(int id, User user);

    void makeNotAdmin(int id, User user);

    void makeUnemployed(int id, User user);
}
