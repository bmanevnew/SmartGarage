package com.company.web.smart_garage.repositories;

import com.company.web.smart_garage.models.repair.Repair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RepairRepository extends JpaRepository<Repair, Long> {

    Optional<Repair> findFirstByName(String name);

    @Query("select r from Repair r where " +
            "(:name is null or r.name like %:name%) and " +
            "(:priceFrom is null or r.price >= :priceFrom) and " +
            "(:priceTo is null or r.price <= :priceTo)")
    Page<Repair> findByParameters(@Param("name") String name,
                                  @Param("priceFrom") Double priceFrom,
                                  @Param("priceTo") Double priceTo,
                                  Pageable pageable);
}
