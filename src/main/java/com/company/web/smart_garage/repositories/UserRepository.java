package com.company.web.smart_garage.repositories;

import com.company.web.smart_garage.models.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long>, CrudRepository<User, Long> {
}
