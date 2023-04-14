package com.company.web.smart_garage.utils.helpers;

import com.company.web.smart_garage.data_transfer_objects.VehicleDto;
import com.company.web.smart_garage.models.Vehicle;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VehicleMapper {

    private final ModelMapper modelMapper;

    public Vehicle dtoToVehicle(VehicleDto dto) {
        return modelMapper.map(dto, Vehicle.class);
    }

    public Vehicle dtoToVehicle(VehicleDto dto, long id) {
        Vehicle vehicle = dtoToVehicle(dto);
        vehicle.setId(id);
        return vehicle;
    }

    public VehicleDto vehicleToDto(Vehicle vehicle) {
        return modelMapper.map(vehicle, VehicleDto.class);
    }
}
