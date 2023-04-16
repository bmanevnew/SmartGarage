package com.company.web.smart_garage.repositories;

import com.company.web.smart_garage.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findFirstByEmail(String email);

    Optional<User> findFirstByUsernameOrEmail(String username, String email);

    Optional<User> findFirstByPhoneNumber(String phoneNumber);

    Page<User> findByFirstName(String firstName, Pageable pageable);

    Page<User> findByLastName(String lastName, Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.vehicles LEFT JOIN FETCH u.visits WHERE u.id = :userId")
    Optional<User> findByIdFetchAll(@Param("userId") Long userId);


    @Query("SELECT u FROM User u LEFT JOIN u.vehicles v LEFT JOIN u.visits vi WHERE " +
            "(:name IS NULL OR u.firstName LIKE %:name%) " +
            "AND (:vehicleModel IS NULL OR v.model LIKE %:vehicleModel%) " +
            "AND (:vehicleMake IS NULL OR v.brand LIKE %:vehicleMake%) " +
            "AND (:fromDate IS NULL OR vi.date >= :fromDate) " +
            "AND (:toDate IS NULL OR vi.date <= :toDate)")
    Page<User> findByFilters(@Param("name") String name,
                             @Param("vehicleModel") String vehicleModel,
                             @Param("vehicleMake") String vehicleMake,
                             @Param("fromDate") LocalDateTime fromDate,
                             @Param("toDate") LocalDateTime toDate,
                             Pageable pageable);
}


