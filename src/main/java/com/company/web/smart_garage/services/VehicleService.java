package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.user.User;
import com.company.web.smart_garage.models.vehicle.Vehicle;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface VehicleService {

    Vehicle getById(long id);

    Vehicle getByLicensePlate(String licensePlate);

    Vehicle getByVin(String vin);

    Page<Vehicle> getByOwner(String username, int page);

    Page<Vehicle> filterAndSort(Map<String, String> params);

    Vehicle create(Vehicle vehicle, User owner);

    Vehicle update(Vehicle vehicle);

    void delete(long id);
}
