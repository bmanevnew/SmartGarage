package com.company.web.smart_garage.repositories;

import com.company.web.smart_garage.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findFirstByEmail(String email);

    Optional<User> findFirstByUsernameOrEmail(String username, String email);

    Optional<User> findFirstByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.vehicles LEFT JOIN FETCH u.visits WHERE u.id = :userId")
    Optional<User> findByIdFetchAll(@Param("userId") Long userId);


    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN u.vehicles v " +
            "LEFT JOIN u.visits vi " +
            "WHERE (:name IS NULL OR u.firstName LIKE %:name%) " +
            "AND (:vehicleModel IS NULL OR v.model LIKE %:vehicleModel%) " +
            "AND (:vehicleMake IS NULL OR v.brand LIKE %:vehicleMake%) " +
            "AND (:fromDate is null or date(vi.date) >= :fromDate) " +
            "AND (:toDate is null or date(vi.date) <= :toDate)")
    Page<User> findByFilters(@Param("name") String name,
                             @Param("vehicleModel") String vehicleModel,
                             @Param("vehicleMake") String vehicleMake,
                             @Param("fromDate") Date fromDate,
                             @Param("toDate") Date toDate,
                             Pageable pageable);

}


