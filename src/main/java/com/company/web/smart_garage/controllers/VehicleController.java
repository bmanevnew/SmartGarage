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
import java.util.stream.Collectors;

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
    public List<VehicleDto> getAll(@RequestParam(required = false, name = "owner-username") String username,
                                   @RequestParam(required = false, name = "owner-phone-number") String phoneNumber,
                                   @RequestParam(required = false, name = "model") String model,
                                   @RequestParam(required = false, name = "brand") String brand,
                                   @RequestParam(required = false, name = "prod-from") Integer prodYearFrom,
                                   @RequestParam(required = false, name = "prod-to") Integer prodYearTo,
                                   Pageable pageable) {
        Long ownerId = null;
        if (username != null) {
            ownerId = userService.getByUsername(username).getId();
        } else if (phoneNumber != null) {
            ownerId = userService.getByPhoneNumber(phoneNumber).getId();
        }
        return vehicleService.getAll(ownerId, model, brand, prodYearFrom, prodYearTo, pageable).getContent()
                .stream().map(vehicleMapper::vehicleToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public VehicleDto create(@RequestBody VehicleDto dto) {
        Vehicle vehicle = vehicleMapper.dtoToVehicle(dto);
        //TODO remove hard coded user when proper authentication is implemented
        return vehicleMapper.vehicleToDto(vehicleService.create(vehicle, userService.getUserById(1)));
    }

    @PutMapping("/{id}")
    public VehicleDto update(@PathVariable long id, @RequestBody VehicleDto dto) {
        Vehicle vehicle = vehicleMapper.dtoToVehicle(dto, id);
        return vehicleMapper.vehicleToDto(vehicleService.update(vehicle));
    }

    @DeleteMapping("/{id}")
    public VehicleDto delete(@PathVariable long id) {
        Vehicle vehicle = vehicleService.getById(id);
        vehicleService.delete(id);
        return vehicleMapper.vehicleToDto(vehicle);
    }
}
