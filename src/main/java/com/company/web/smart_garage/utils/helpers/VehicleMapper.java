package com.company.web.smart_garage.utils.helpers;

import com.company.web.smart_garage.models.vehicle.Vehicle;
import com.company.web.smart_garage.models.vehicle.VehicleDto;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public Vehicle dtoToVehicle(VehicleDto dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(dto.getVin());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setProductionYear(dto.getProductionYear());
        return vehicle;
    }

    public Vehicle dtoToVehicle(VehicleDto dto, long id) {
        Vehicle vehicle = dtoToVehicle(dto);
        vehicle.setId(id);
        return vehicle;
    }

    public VehicleDto vehicleToDto(Vehicle vehicle) {
        VehicleDto dto = new VehicleDto();
        dto.setVin(vehicle.getVin());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setModel(vehicle.getModel());
        dto.setBrand(vehicle.getBrand());
        dto.setProductionYear(vehicle.getProductionYear());
        return dto;
    }
}
