package com.company.web.smart_garage.repositories;

import com.company.web.smart_garage.models.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query("select v from Visit v left join fetch v.repairs where v.id = :id")
    Optional<Visit> findByIdFetchRepairs(@Param("id") long id);

    @Query("select v from Visit v where " +
            "(:visitorId is null or v.visitor.id = :visitorId) and " +
            "(:vehicleId is null or v.vehicle.id = :vehicleId) and " +
            "(:dateFrom is null or date(v.date) >= :dateFrom) and " +
            "(:dateTo is null or date(v.date) <= :dateTo)")
    Page<Visit> findByParameters(@Param("visitorId") Long visitorId,
                                 @Param("vehicleId") Long vehicleId,
                                 @Param("dateFrom") Date dateFrom,
                                 @Param("dateTo") Date dateTo,
                                 Pageable pageable);
}
