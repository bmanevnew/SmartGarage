package com.company.web.smart_garage.controllers;

import com.company.web.smart_garage.models.vehicle.Vehicle;
import com.company.web.smart_garage.models.vehicle.VehicleDto;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.services.VehicleService;
import com.company.web.smart_garage.utils.helpers.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;
    private final UserService userService;

    @GetMapping("/{id}")
    public VehicleDto getById(@PathVariable long id) {
        return vehicleMapper.vehicleToDto(vehicleService.getById(id));
    }

    @GetMapping(params = "license-plate")
    public VehicleDto getByLicensePlate(@RequestParam(name = "license-plate") String licensePlate) {
        return vehicleMapper.vehicleToDto(vehicleService.getByLicensePlate(licensePlate));
    }

    @GetMapping(params = "vin")
    public VehicleDto getByVin(@RequestParam(name = "vin") String vin) {
        return vehicleMapper.vehicleToDto(vehicleService.getByVin(vin));
    }

    @GetMapping
    public List<VehicleDto> getAll(@RequestParam(required = false, name = "owner-id") Long ownerId,
                                   @RequestParam(required = false, name = "owner-username") String username,
                                   @RequestParam(required = false, name = "owner-phone-number") String phoneNumber,
                                   @RequestParam(required = false, name = "model") String model,
                                   @RequestParam(required = false, name = "brand") String brand,
                                   @RequestParam(required = false, name = "prod-from") Integer prodYearFrom,
                                   @RequestParam(required = false, name = "prod-to") Integer prodYearTo,
                                   Pageable pageable) {
        Long actualOwnerId = getActualOwnerId(ownerId, username, phoneNumber);
        return vehicleService.getAll(actualOwnerId, model, brand, prodYearFrom, prodYearTo, pageable)
                .map(vehicleMapper::vehicleToDto).getContent();
    }

    private Long getActualOwnerId(Long ownerId, String username, String phoneNumber) {
        Long actualId = null;
        if (username != null) {
            actualId = userService.getByUsername(username).getId();
        } else if (ownerId != null) {
            actualId = ownerId;
        } else if (phoneNumber != null) {
            actualId = userService.getByPhoneNumber(phoneNumber).getId();
        }
        return actualId;
    }

    @PostMapping
    public VehicleDto create(@RequestBody VehicleDto dto, @RequestParam("owner-email") String email) {
        Vehicle vehicle = vehicleMapper.dtoToVehicle(dto);
        return vehicleMapper.vehicleToDto(vehicleService.create(vehicle, email));
    }

    @PutMapping("/{id}")
    public VehicleDto update(@PathVariable long id, @RequestBody VehicleDto dto) {
        Vehicle vehicle = vehicleMapper.dtoToVehicle(dto, id);
        return vehicleMapper.vehicleToDto(vehicleService.update(vehicle));
    }

    @DeleteMapping("/{id}")
    public VehicleDto delete(@PathVariable long id) {
        return vehicleMapper.vehicleToDto(vehicleService.delete(id));
    }
}
