package com.company.web.smart_garage.utils.mappers;

import com.company.web.smart_garage.data_transfer_objects.VehicleDto;
import com.company.web.smart_garage.data_transfer_objects.VehicleDtoCreate;
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

    public Vehicle dtoToVehicle(VehicleDtoCreate dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setVin(dto.getVin());
        vehicle.setModel(dto.getModel());
        vehicle.setBrand(dto.getBrand());
        vehicle.setProductionYear(Integer.parseInt(dto.getProductionYear()));
        return modelMapper.map(dto, Vehicle.class);
    }

    public VehicleDto vehicleToDto(Vehicle vehicle) {
        return modelMapper.map(vehicle, VehicleDto.class);
    }
}
