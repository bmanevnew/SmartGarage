package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.enums.VehicleParam;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.user.User;
import com.company.web.smart_garage.models.vehicle.Vehicle;
import com.company.web.smart_garage.repositories.VehicleRepository;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.services.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.company.web.smart_garage.utils.Constants.*;

@RequiredArgsConstructor
@Service
public class VehicleServiceImpl implements VehicleService {

    public static final int MIN_PROD_YEAR = 1886;
    public static final int PAGE_SIZE = 2;
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
    public Page<Vehicle> getByOwner(String username, int page) {
        page -= 1;
        if (page < 0) throw new InvalidParamException(PAGE_INVALID);
        User owner = userService.getByUsername(username);
        Page<Vehicle> repoPage = vehicleRepository.findByOwner(owner, PageRequest.of(page, PAGE_SIZE));
        if (repoPage.getContent().isEmpty() && page != 0) {
            throw new InvalidParamException(PAGE_INVALID);
        }
        return repoPage;
    }

    @Override
    public Page<Vehicle> filterAndSort(Map<String, String> params) {
        if (params.containsKey(VehicleParam.LICENSE_PLATE.getParamName())) {
            return new PageImpl<>(List.of(getByLicensePlate(params.get(VehicleParam.LICENSE_PLATE.getParamName()))));
        }
        if (params.containsKey(VehicleParam.VIN.getParamName())) {
            return new PageImpl<>(List.of(getByVin(params.get(VehicleParam.VIN.getParamName()))));
        }
        int page;
        try {
            page = params.containsKey(VehicleParam.PAGE.getParamName()) ?
                    Integer.parseInt(params.get(VehicleParam.PAGE.getParamName())) : 1;
        } catch (NumberFormatException e) {
            throw new InvalidParamException(PAGE_INVALID);
        }
        if (page < 0) throw new InvalidParamException(PAGE_INVALID);

        if (params.containsKey(VehicleParam.OWNER.getParamName())) {
            return getByOwner(params.get(VehicleParam.OWNER.getParamName()), page);
        }

        String model = params.getOrDefault(VehicleParam.MODEL.getParamName(), null);
        String brand = params.getOrDefault(VehicleParam.BRAND.getParamName(), null);
        Integer prodYearFrom;
        Integer prodYearTo;
        try {
            prodYearFrom = params.containsKey(VehicleParam.PROD_YEAR_FROM.getParamName()) ?
                    Integer.parseInt(params.get(VehicleParam.PROD_YEAR_FROM.getParamName())) : null;
            prodYearTo = params.containsKey(VehicleParam.PROD_YEAR_TO.getParamName()) ?
                    Integer.parseInt(params.get(VehicleParam.PROD_YEAR_TO.getParamName())) : null;
        } catch (NumberFormatException e) {
            throw new InvalidParamException(VEHICLE_PROD_YEAR_INVALID_FORMAT);
        }
        validateProdYearInterval(prodYearFrom, prodYearTo);
        String sortBy = params.getOrDefault(VehicleParam.SORT_BY.getParamName(), null);
        String sortOrder = params.getOrDefault(VehicleParam.SORT_ORD.getParamName(), null);

        Pageable pageable = generatePageable(page - 1, generateSort(sortBy, sortOrder));

        Page<Vehicle> repoPage = vehicleRepository.findByParameters(model, brand, prodYearFrom, prodYearTo, pageable);
        if (repoPage.getContent().isEmpty() && page != 0) {
            throw new InvalidParamException(PAGE_INVALID);
        }
        return repoPage;
    }

    @Override
    public Vehicle create(Vehicle vehicle, User owner) {
        vehicle.setOwner(owner);
        validateLicensePlate(vehicle.getLicensePlate());
        validateProdYear(vehicle.getProductionYear());
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle update(Vehicle vehicle) {
        validateLicensePlate(vehicle.getLicensePlate());
        validateProdYear(vehicle.getProductionYear());
        return vehicleRepository.save(vehicle);
    }

    @Override
    public void delete(long id) {
        vehicleRepository.deleteById(id);
    }

    private Sort generateSort(String sortBy, String sortOrder) {
        boolean hasSort = validateSortBy(sortBy);
        boolean hasSortOrd = validateSortOrder(sortOrder);
        if (!hasSort) {
            return Sort.unsorted();
        } else {
            if (!hasSortOrd || sortBy.equals("asc")) {
                return Sort.by(sortBy);
            } else {
                return Sort.by(sortBy).descending();
            }
        }
    }

    private Pageable generatePageable(int page, Sort sort) {
        return PageRequest.of(page, PAGE_SIZE, sort);
    }

    private void validateId(long id) {
        if (id <= 0) {
            throw new InvalidParamException("Id must be positive.");
        }
    }

    private void validateLicensePlate(String licensePlate) {
        if (licensePlate != null) {
            if (!licensePlate.matches("^[ABEKMHOPCTYX]{1,2} \\d{4} [ABEKMHOPCTYX]{2}$")) {
                throw new InvalidParamException(VEHICLE_PLATE_INVALID_FORMAT);
            }
            Set<String> validAreaCodes = Set.of(VALID_AREA_CODES.split(","));
            String areaCode = licensePlate.split(" ")[0];
            if (!validAreaCodes.contains(areaCode)) {
                throw new InvalidParamException(VEHICLE_PLATE_INVALID_AREA_CODE);
            }
        }
    }


    private void validateVin(String vin) {
        if (vin != null) {
            if (!vin.matches("^[A-Z\\d]{17}$")) {
                throw new InvalidParamException(VEHICLE_VIN_INVALID_FORMAT);
            }
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

    private boolean validateSortBy(String sortBy) {
        if (sortBy != null) {
            String[] validSortByParams = {"model", "brand", "productionYear"};
            if (!List.of(validSortByParams).contains(sortBy)) {
                throw new InvalidParamException(SORT_BY_INVALID);
            }
            return true;
        }
        return false;
    }

    private boolean validateSortOrder(String sortOrder) {
        if (sortOrder != null) {
            String[] validSortByParams = {"asc", "desc"};
            if (!List.of(validSortByParams).contains(sortOrder)) {
                throw new InvalidParamException(SORT_ORDER_INVALID);
            }
            return true;
        }
        return false;
    }
}
