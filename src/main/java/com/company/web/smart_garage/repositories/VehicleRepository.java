package com.company.web.smart_garage.repositories;

import com.company.web.smart_garage.models.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("select v from Vehicle v join fetch v.visits where v.id = :id")
    Optional<Vehicle> findByIdFetchAll(@Param("id") long id);

    Optional<Vehicle> findFirstByLicensePlate(String licensePlate);

    Optional<Vehicle> findFirstByVin(String vin);

    Optional<Vehicle> findFirstByLicensePlateOrVin(String licensePlate, String vin);

    @Query("select v from Vehicle v where " +
            "(:ownerId is null or v.owner.id = :ownerId) and " +
            "(:model is null or v.model like %:model%) and " +
            "(:brand is null or v.brand like %:brand%) and " +
            "(:prodYearFrom is null or v.productionYear >= :prodYearFrom) and " +
            "(:prodYearTo is null or v.productionYear <= :prodYearTo)")
    Page<Vehicle> findByParameters(@Param("ownerId") Long ownerId,
                                   @Param("model") String model,
                                   @Param("brand") String brand,
                                   @Param("prodYearFrom") Integer prodYearFrom,
                                   @Param("prodYearTo") Integer prodYearTo,
                                   Pageable pageable);
}
