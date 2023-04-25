package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.Repair;
import com.company.web.smart_garage.models.Visit;
import com.company.web.smart_garage.models.VisitPdfExporter;
import com.company.web.smart_garage.repositories.VisitRepository;
import com.company.web.smart_garage.services.RepairService;
import com.company.web.smart_garage.services.VisitService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static com.company.web.smart_garage.utils.Constants.*;

@RequiredArgsConstructor
@Service
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final EmailSenderServiceImpl emailSenderService;
    private final RepairService repairService;

    @Override
    public Visit getById(long id) {
        validateId(id);
        return visitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Visit", id));
    }

    @Override
    public Page<Visit> getAll(Long visitorId, Long vehicleId, LocalDate dateFrom, LocalDate dateTo, Pageable pageable) {
        validateId(visitorId);
        validateId(vehicleId);
        validateDateInterval(dateFrom, dateTo);
        validateSortProperties(pageable.getSort());

        Page<Visit> page = visitRepository.findByParameters(visitorId, vehicleId,
                (dateFrom == null ? null : java.sql.Date.valueOf(dateFrom)),
                (dateTo == null ? null : java.sql.Date.valueOf(dateTo)), pageable);
        if (pageable.getPageNumber() >= page.getTotalPages() && pageable.getPageNumber() != 0) {
            throw new InvalidParamException(PAGE_IS_INVALID);
        }
        return page;
    }

    @Override
    public Visit create(Visit visit) {
        visit.setDate(LocalDateTime.now());
        return visitRepository.save(visit);
    }

    @Override
    public Visit update(Visit visit) {
        if (visit.getId() == null || visit.getId() <= 0) throw new InvalidParamException(ID_INVALID);
        Visit visitPersistent = getById(visit.getId());
        visit.setVehicle(visitPersistent.getVehicle());
        visit.setVisitor(visitPersistent.getVisitor());
        visit.setDate(visitPersistent.getDate());
        return visitRepository.save(visit);
    }

    @Override
    public Visit addRepair(long visitId, long repairId) {
        validateId(repairId);
        validateId(visitId);
        Visit visit = getById(visitId);
        if (visit.getRepairs().stream().anyMatch(repair -> repair.getId().equals(repairId)))
            throw new InvalidParamException(String.format("Visit %d already has repair %d.", visitId, repairId));
        Set<Repair> set = visit.getRepairs();
        set.add(repairService.getById(repairId));
        visit.setRepairs(set);
        return visitRepository.save(visit);
    }

    @Override
    public Visit removeRepair(long visitId, long repairId) {
        validateId(repairId);
        validateId(visitId);
        Visit visit = getById(visitId);
        if (visit.getRepairs().stream().noneMatch(repair -> repair.getId().equals(repairId)))
            throw new InvalidParamException(String.format("Visit %d has no repair %d.", visitId, repairId));
        Set<Repair> set = visit.getRepairs();
        set.remove(repairService.getById(repairId));
        visit.setRepairs(set);
        return visitRepository.save(visit);
    }

    @Override
    public Visit delete(long id) {
        validateId(id);
        Visit visit = visitRepository.findByIdFetchRepairs(id)
                .orElseThrow(() -> new EntityNotFoundException("Visit", id));
        visitRepository.deleteById(id);
        return visit;
    }

    @Override
    public void sendPdfToMail(Visit visit, Double rate) throws MessagingException {

        VisitPdfExporter exporter = new VisitPdfExporter(visit, rate);
        ByteArrayOutputStream baos = exporter.export();

        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

        String fileName = String.format(FILE_NAME, visit.getId());
        String toEmail = visit.getVisitor().getEmail();
        String subject = String.format(REPORT_EMAIL_SUBJECT_FORMAT, visit.getId());
        String body = String.format(REPORT_EMAIL_BODY_FORMAT, visit.getId());
        emailSenderService.sendEmailWithAttachment(toEmail, subject, body, fileName, resource);
    }

    private void validateId(Long id) {
        if (id != null && id <= 0) {
            throw new InvalidParamException(ID_MUST_BE_POSITIVE);
        }
    }

    private void validateSortProperties(Sort sort) {
        sort.get().forEach(order -> validateSortingProperty(order.getProperty()));
    }

    private void validateSortingProperty(String property) {
        if (!property.equals("date")) {
            throw new InvalidParamException(String.format(SORT_PROPERTY_S_IS_INVALID, property));
        }
    }

    private boolean validateDate(LocalDate date) {
        if (date != null) {
            if (date.isAfter(LocalDate.now())) {
                throw new InvalidParamException(VISIT_DATE_IN_FUTURE);
            }
            return true;
        }
        return false;
    }

    private void validateDateInterval(LocalDate dateFrom, LocalDate dateTo) {
        if (validateDate(dateFrom) & validateDate(dateTo)) {
            if (dateFrom.isAfter(dateTo)) {
                throw new InvalidParamException(VISIT_DATE_INTERVAL_INVALID);
            }
        }
    }
}
