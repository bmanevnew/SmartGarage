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
import java.util.Set;

import static com.company.web.smart_garage.utils.Constants.*;

@RequiredArgsConstructor
@Service
public class VehicleServiceImpl implements VehicleService {

    public static final int MIN_PROD_YEAR = 1886;
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
    public Page<Vehicle> getAll(Long ownerId, String model, String brand, Integer prodYearFrom, Integer prodYearTo,
                                Pageable pageable) {
        validateId(ownerId);
        validateProdYearInterval(prodYearFrom, prodYearTo);
        validateSortProperties(pageable.getSort());

        Page<Vehicle> page = vehicleRepository.findByParameters(ownerId, model, brand, prodYearFrom, prodYearTo, pageable);
        if (pageable.getPageNumber() >= page.getTotalPages()) {
            throw new InvalidParamException(PAGE_IS_INVALID);
        }
        return page;
    }

    @Override
    public Vehicle create(Vehicle vehicle, String email) {
        User owner;
        try {
            owner = userService.getByEmail(email);
        } catch (EntityNotFoundException e) {
            //TODO  should be implemented in user
//            owner = userService.create(email);
            owner = userService.getUserById(1);
        }
        vehicle.setOwner(owner);
        validateLicensePlate(vehicle.getLicensePlate());
        validateProdYear(vehicle.getProductionYear());
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle update(Vehicle vehicle) {
        if (!vehicleRepository.existsById(vehicle.getId()))
            throw new EntityNotFoundException("Vehicle", vehicle.getId());
        validateLicensePlate(vehicle.getLicensePlate());
        validateVin(vehicle.getVin());
        validateProdYear(vehicle.getProductionYear());
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle delete(long id) {
        validateId(id);
        Vehicle vehicle = vehicleRepository.findByIdFetchVisits(id)
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
            if (!licensePlate.matches("^[ABEKMHOPCTYX]{1,2} \\d{4} [ABEKMHOPCTYX]{2}$")) {
                throw new InvalidParamException(VEHICLE_PLATE_INVALID_FORMAT);
            }
            Set<String> validAreaCodes = Set.of(VEHICLE_VALID_AREA_CODES.split(","));
            String areaCode = licensePlate.split(" ")[0];
            if (!validAreaCodes.contains(areaCode)) {
                throw new InvalidParamException(VEHICLE_PLATE_INVALID_AREA_CODE);
            }
        }
    }


    private void validateVin(String vin) {
        if (vin != null && !vin.matches("^[A-Z\\d]{17}$")) {
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
