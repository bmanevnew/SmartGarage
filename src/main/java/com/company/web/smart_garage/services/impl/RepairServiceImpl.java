package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.exceptions.EntityDuplicationException;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.Repair;
import com.company.web.smart_garage.repositories.RepairRepository;
import com.company.web.smart_garage.services.RepairService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static com.company.web.smart_garage.utils.Constants.*;

@RequiredArgsConstructor
@Service
public class RepairServiceImpl implements RepairService {

    public static final String FILTER_NAME = "deletedRepairFilter";
    public static final String FILTER_PARAM = "isDeleted";
    private final RepairRepository repairRepository;
    private final EntityManager entityManager;

    @Override
    public Repair getById(long id) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(FILTER_NAME);
        filter.setParameter(FILTER_PARAM, false);

        validateId(id);
        Repair repair = repairRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Repair", id));

        session.disableFilter(FILTER_NAME);
        return repair;
    }

    @Override
    public Repair getByName(String name) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(FILTER_NAME);
        filter.setParameter(FILTER_PARAM, false);

        Repair repair = repairRepository.findFirstByName(name).orElseThrow(
                () -> new EntityNotFoundException("Repair", "name", name));

        session.disableFilter(FILTER_NAME);
        return repair;
    }

    @Override
    public Page<Repair> getAll(String name, Double priceFrom, Double priceTo, Pageable pageable) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(FILTER_NAME);
        filter.setParameter(FILTER_PARAM, false);

        validatePriceRange(priceFrom, priceTo);
        validateSortProperties(pageable.getSort());

        Page<Repair> page = repairRepository.findByParameters(name, priceFrom, priceTo, pageable);
        if (pageable.getPageNumber() >= page.getTotalPages()) {
            throw new InvalidParamException(PAGE_IS_INVALID);
        }

        session.disableFilter(FILTER_NAME);
        return page;
    }

    @Override
    public Repair create(Repair repair) {
        validatePrice(repair.getPrice());
        try {
            getByName(repair.getName());
        } catch (EntityNotFoundException e) {
            return repairRepository.save(repair);
        }
        throw new EntityDuplicationException(String.format(REPAIR_WITH_NAME_S_ALREADY_EXISTS, repair.getName()));
    }

    //soft deletes updated repair and creates a new one with new data
    @Override
    public Repair update(Repair repair) {
        validateId(repair.getId());
        validatePrice(repair.getPrice());
        if (!repairRepository.existsById(repair.getId())) throw new EntityNotFoundException("Repair", repair.getId());

        boolean nameAlreadyExists = true;
        try {
            long id = getByName(repair.getName()).getId();
            if (repair.getId().equals(id)) nameAlreadyExists = false;
        } catch (EntityNotFoundException e) {
            nameAlreadyExists = false;
        }
        if (nameAlreadyExists) throw new EntityDuplicationException(
                String.format(REPAIR_WITH_NAME_S_ALREADY_EXISTS, repair.getName()));

        repairRepository.deleteById(repair.getId());
        repair.setId(null);
        return repairRepository.save(repair);
    }

    //soft-delete
    @Override
    public Repair delete(long id) {
        validateId(id);
        Repair repair = getById(id);
        repairRepository.deleteById(id);
        return repair;
    }

    private void validateSortProperties(Sort sort) {
        sort.get().forEach(order -> validateSortingProperty(order.getProperty()));
    }

    private void validateSortingProperty(String property) {
        switch (property) {
            case "name", "price" -> {
            }
            default -> throw new InvalidParamException(String.format(SORT_PROPERTY_S_IS_INVALID, property));
        }
    }

    private void validateId(Long id) {
        if (id != null && id <= 0) throw new InvalidParamException(ID_MUST_BE_POSITIVE);
    }

    private boolean validatePrice(Double price) {
        if (price != null) {
            if (price <= 0) throw new InvalidParamException(PRICE_IS_INVALID);
            return true;
        }
        return false;
    }

    private void validatePriceRange(Double priceFrom, Double priceTo) {
        if (validatePrice(priceFrom) & validatePrice(priceTo)) {
            if (priceFrom > priceTo) throw new InvalidParamException(PRICE_INTERVAL_IS_INVALID);
        }
    }
}
