package com.company.web.smart_garage.controllers;

import com.company.web.smart_garage.models.vehicle.Vehicle;
import com.company.web.smart_garage.models.vehicle.VehicleDto;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.services.VehicleService;
import com.company.web.smart_garage.utils.helpers.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;
    private final UserService userService;

    @GetMapping
    public List<VehicleDto> filterAndSort(@RequestParam Map<String, String> params) {
        return vehicleService.filterAndSort(params).getContent().stream()
                .map(vehicleMapper::vehicleToDto)
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
