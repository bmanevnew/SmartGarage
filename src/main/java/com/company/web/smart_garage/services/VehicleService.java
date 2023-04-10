package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.user.User;
import com.company.web.smart_garage.models.vehicle.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleService {

    Vehicle getById(long id);

    Vehicle getByLicensePlate(String licensePlate);

    Vehicle getByVin(String vin);

    Page<Vehicle> getAll(Long ownerId, String model, String brand, Integer prodYearFrom, Integer prodYearTo,
                         Pageable pageable);

    Vehicle create(Vehicle vehicle, User owner);

    Vehicle update(Vehicle vehicle);

    void delete(long id);
}
