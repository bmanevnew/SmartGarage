package com.company.web.smart_garage.repositories;

import com.company.web.smart_garage.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
