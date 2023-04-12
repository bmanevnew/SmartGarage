package com.company.web.smart_garage.repositories;

import com.company.web.smart_garage.models.visit.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query("select v from Visit v where " +
            "(:visitorId is null or v.visitor.id = :visitorId) and " +
            "(:vehicleId is null or v.vehicle.id = :vehicleId) and " +
            "(:dateFrom is null or v.date >= :dateFrom) and " +
            "(:dateTo is null or v.date <= :dateTo)")
    Page<Visit> findByParameters(@Param("visitorId") Long visitorId,
                                 @Param("vehicleId") Long vehicleId,
                                 @Param("dateFrom") LocalDate dateFrom,
                                 @Param("dateTo") LocalDate dateTo,
                                 Pageable pageable);
}
