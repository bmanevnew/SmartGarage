package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.models.Vehicle;
import com.company.web.smart_garage.repositories.VehicleRepository;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.services.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.company.web.smart_garage.utils.Constants.*;

@RequiredArgsConstructor
@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserService userService;

    @Override
    public Vehicle getById(long id) {
        validateId(id);
        return vehicleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Vehicle", id));
    }

    @Override
    public Vehicle getByLicensePlate(String licensePlate) {
        validateLicensePlate(licensePlate);
        return vehicleRepository.findFirstByLicensePlate(licensePlate)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", "license plate", licensePlate));
    }

    @Override
    public Vehicle getByVin(String vin) {
        validateVin(vin);
        return vehicleRepository.findFirstByVin(vin)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", "vin number", vin));
    }

    @Override
    public Vehicle getByLicensePlateOrVin(String licensePlateOrVin) {
        return vehicleRepository.findFirstByLicensePlateOrVin(licensePlateOrVin, licensePlateOrVin)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", "license plate or vin", licensePlateOrVin));
    }

    @Override
    public Page<Vehicle> getAll(Long ownerId, String model, String brand, Integer prodYearFrom, Integer prodYearTo,
                                Pageable pageable) {
        validateId(ownerId);
        validateProdYearInterval(prodYearFrom, prodYearTo);
        validateSortProperties(pageable.getSort());

        Page<Vehicle> page = vehicleRepository.findByParameters(ownerId, model, brand, prodYearFrom, prodYearTo, pageable);
        if (pageable.getPageNumber() >= page.getTotalPages() && pageable.getPageNumber() != 0) {
            throw new InvalidParamException(PAGE_IS_INVALID);
        }
        return page;
    }

    @Override
    public Vehicle create(Vehicle vehicle, User owner) {
        try {
            if (owner == null || owner.getEmail() == null) throw new InvalidParamException(INVALID_OWNER);
            owner = userService.getByEmail(owner.getEmail());
        } catch (EntityNotFoundException e) {
            if (owner == null || owner.getPhoneNumber() == null) throw new InvalidParamException(INVALID_OWNER);
            owner = userService.create(owner);
        }
        vehicle.setOwner(owner);
        validateLicensePlate(vehicle.getLicensePlate());
        validateProdYear(vehicle.getProductionYear());
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle update(Vehicle vehicle) {
        Vehicle persistentVehicle = getById(vehicle.getId());

        validateLicensePlate(vehicle.getLicensePlate());
        persistentVehicle.setLicensePlate(vehicle.getLicensePlate());
        validateVin(vehicle.getVin());
        persistentVehicle.setVin(vehicle.getVin());
        validateProdYear(vehicle.getProductionYear());
        persistentVehicle.setProductionYear(vehicle.getProductionYear());
        persistentVehicle.setBrand(vehicle.getBrand());
        persistentVehicle.setModel(vehicle.getModel());

        return vehicleRepository.save(persistentVehicle);
    }

    @Override
    public Vehicle delete(long id) {
        validateId(id);
        Vehicle vehicle = vehicleRepository.findByIdFetchAll(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id));
        vehicleRepository.deleteById(id);
        return vehicle;
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
        switch (property) {
            case "licensePlate", "model", "brand", "productionYear" -> {
            }
            default -> throw new InvalidParamException(String.format(SORT_PROPERTY_S_IS_INVALID, property));
        }
    }

    private void validateLicensePlate(String licensePlate) {
        if (licensePlate != null) {
            if (!licensePlate.matches(VEHICLE_LICENSE_PLATE_REGEX)) {
                throw new InvalidParamException(VEHICLE_PLATE_INVALID_FORMAT);
            }
        }
    }


    private void validateVin(String vin) {
        if (vin != null && !vin.matches(VEHICLE_VIN_REGEX)) {
            throw new InvalidParamException(VEHICLE_VIN_INVALID_FORMAT);
        }
    }

    private boolean validateProdYear(Integer prodYear) {
        if (prodYear != null) {
            if (prodYear < MIN_PROD_YEAR || prodYear > LocalDate.now().getYear()) {
                throw new InvalidParamException(VEHICLE_PROD_YEAR_INVALID);
            }
            return true;
        }
        return false;
    }

    private void validateProdYearInterval(Integer prodYearFrom, Integer prodYearTo) {
        if (validateProdYear(prodYearFrom) & validateProdYear(prodYearTo)) {
            if (prodYearFrom > prodYearTo) {
                throw new InvalidParamException(VEHICLE_PROD_YEAR_INTERVAL_INVALID);
            }
        }
    }
}
