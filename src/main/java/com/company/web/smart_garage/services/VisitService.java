package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.visit.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface VisitService {

    Visit getById(long id);

    Page<Visit> getAll(Long visitorId, Long vehicleId, LocalDate dateFrom, LocalDate dateTo, Pageable pageable);

    Visit create(Visit visit);

    Visit update(Visit visit);

    Visit delete(long id);
}
