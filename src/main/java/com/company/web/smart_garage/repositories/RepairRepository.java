package com.company.web.smart_garage.repositories;

import com.company.web.smart_garage.models.repair.Repair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepairRepository extends JpaRepository<Repair, Long> {

    @Query("select r from Visit v join v.repairs r where " +
            "(:visitId is null or v.id = :visitId) and " +
            "(:name is null or r.name like %:name%) and " +
            "(:priceFrom is null or r.price >= :priceFrom) and " +
            "(:priceTo is null or r.price <= :priceTo) and " +
            "(:isActive is null or r.isActive = :isActive)")
    Page<Repair> findByParameters(@Param("visitId") Long visitId,
                                  @Param("name") String name,
                                  @Param("priceFrom") Double priceFrom,
                                  @Param("priceTo") Double priceTo,
                                  @Param("isActive") Boolean isActive,
                                  Pageable pageable);
}
