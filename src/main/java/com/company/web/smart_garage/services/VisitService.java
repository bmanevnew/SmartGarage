package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.Visit;
import com.company.web.smart_garage.models.user.User;
import com.company.web.smart_garage.models.vehicle.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface VisitService {

    Visit getById(long id);

    Page<Visit> getAll(Long visitorId, Long vehicleId, LocalDate dateFrom, LocalDate dateTo, Pageable pageable);

    Visit create(User visitor, Vehicle vehicle);

    Visit update(Visit visit);

    void delete(long id);
}
