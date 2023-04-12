package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.visit.Visit;
import com.company.web.smart_garage.repositories.VisitRepository;
import com.company.web.smart_garage.services.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.company.web.smart_garage.utils.Constants.*;

@RequiredArgsConstructor
@Service
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;

    @Override
    public Visit getById(long id) {
        return visitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Visit", id));
    }

    @Override
    public Page<Visit> getAll(Long visitorId, Long vehicleId, LocalDate dateFrom, LocalDate dateTo, Pageable pageable) {
        validateId(visitorId);
        validateId(vehicleId);
        validateDateInterval(dateFrom, dateTo);
        validateSortProperties(pageable.getSort());

        Page<Visit> page = visitRepository.findByParameters(visitorId, vehicleId, dateFrom, dateTo, pageable);
        if (pageable.getPageNumber() >= page.getTotalPages()) {
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
        return visitRepository.save(visit);
    }

    @Override
    public Visit delete(long id) {
        validateId(id);
        Visit visit = getById(id);
        visitRepository.deleteById(id);
        return visit;
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
