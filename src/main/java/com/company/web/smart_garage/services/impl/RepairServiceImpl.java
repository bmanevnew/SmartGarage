package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.repair.Repair;
import com.company.web.smart_garage.repositories.RepairRepository;
import com.company.web.smart_garage.services.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.company.web.smart_garage.utils.Constants.ID_MUST_BE_POSITIVE;
import static com.company.web.smart_garage.utils.Constants.PAGE_IS_INVALID;

@RequiredArgsConstructor
@Service
public class RepairServiceImpl implements RepairService {

    private final RepairRepository repairRepository;

    @Override
    public Repair getById(long id) {
        return repairRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Repair", id));
    }

    @Override
    public Page<Repair> getAll(Long visitId, String name, Double priceFrom, Double priceTo, Boolean isActive,
                               Pageable pageable) {
        validateId(visitId);
        validatePriceRange(priceFrom, priceTo);

        Page<Repair> page = repairRepository.findByParameters(visitId, name, priceFrom, priceTo, isActive, pageable);
        if (pageable.getPageNumber() >= page.getTotalPages()) {
            throw new InvalidParamException(PAGE_IS_INVALID);
        }
        return page;
    }

    @Override
    public Repair create(Repair repair) {
        validatePrice(repair.getPrice());
        return repairRepository.save(repair);
    }

    @Override
    public Repair update(Repair repair) {
        validatePrice(repair.getPrice());
        return repairRepository.save(repair);
    }

    @Override
    public void delete(long id) {

    }

    private void validateId(Long id) {
        if (id != null && id <= 0) throw new InvalidParamException(ID_MUST_BE_POSITIVE);
    }

    private boolean validatePrice(Double price) {
        if (price != null) {
            if (price <= 0) throw new InvalidParamException("Price is invalid.");
            return true;
        }
        return false;
    }

    private void validatePriceRange(Double priceFrom, Double priceTo) {
        if (validatePrice(priceFrom) & validatePrice(priceTo)) {
            if (priceFrom > priceTo) throw new InvalidParamException("Price range is invalid.");
        }
    }
}
