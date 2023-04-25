package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.Visit;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;

public interface VisitService {

    Visit getById(long id);

    Page<Visit> getAll(Long visitorId, Long vehicleId, LocalDate dateFrom, LocalDate dateTo, Pageable pageable);

    Visit create(Visit visit);

    Visit update(Visit visit);

    Visit addRepair(long visitId, long repairId);

    Visit removeRepair(long visitId, long repairId);

    Visit delete(long id);

    void generatePdf(HttpServletResponse response, Visit visit, Double rate) throws IOException, MessagingException;
}
