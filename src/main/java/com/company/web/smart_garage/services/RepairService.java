package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.repair.Repair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RepairService {

    Repair getById(long id);

    Repair getByName(String name);

    Page<Repair> getAll(String name, Double priceFrom, Double priceTo, Pageable pageable);

    Repair create(Repair repair);

    Repair update(Repair repair);

    Repair delete(long id);
}
