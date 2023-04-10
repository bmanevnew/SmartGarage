package com.company.web.smart_garage.repositories;

import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByUsername(String username);

    Optional<User> findFirstByEmail(String email);

    Optional<User> findFirstByPhoneNumber(String phoneNumber);

    List<User> findByFirstName(String firstName);

    List<User> findByLastName(String lastName);

    List<User> findByRolesIn(List<Role> roles);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.vehicles WHERE u.id = :userId")
    User findByIdWithVehicles(@Param("userId") Long userId);
}
