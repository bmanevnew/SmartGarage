package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.repair.Repair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RepairService {

    Repair getById(long id);

    Page<Repair> getAll(Long visitId, String name, Double priceFrom, Double priceTo, Boolean isActive, Pageable pageable);

    Repair create(Repair repair);

    Repair update(Repair repair);

    void delete(long id);
}
