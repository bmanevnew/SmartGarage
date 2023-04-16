package com.company.web.smart_garage.utils.mappers;

import com.company.web.smart_garage.data_transfer_objects.*;
import com.company.web.smart_garage.models.Repair;
import com.company.web.smart_garage.models.Visit;
import com.company.web.smart_garage.services.RepairService;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.services.VehicleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class VisitMapper {

    private final UserService userService;
    private final VehicleService vehicleService;
    private final RepairService repairService;
    private final ModelMapper modelMapper;

    public VisitDtoOut visitToDto(Visit visit) {
        VisitDtoOut dto = new VisitDtoOut();
        dto.setVehicleDto(modelMapper.map(visit.getVehicle(), VehicleDto.class));
        dto.setUserDtoOut(modelMapper.map(visit.getVisitor(), UserDtoOut.class));
        dto.setRepairs(visit.getRepairs().stream()
                .map(repair -> modelMapper.map(repair, RepairDto.class))
                .collect(Collectors.toSet()));
        dto.setDate(visit.getDate().toLocalDate());
        return dto;
    }

    public Visit dtoToVisit(VisitDtoIn visitDto) {
        Visit visit = new Visit();
        visit.setVisitor(userService.getById(visitDto.getUserId()));
        visit.setVehicle(vehicleService.getById(visitDto.getVehicleId()));
        visit.setRepairs(getRepairsFromIds(visitDto.getRepairIds()));
        return visit;
    }

    public Visit dtoToVisit(VisitDtoIn visitDto, long id) {
        Visit visit = dtoToVisit(visitDto);
        visit.setId(id);
        return visit;
    }

    private Set<Repair> getRepairsFromIds(Set<Long> ids) {
        if (ids == null) return new HashSet<>();
        return ids.stream().map(repairService::getById).collect(Collectors.toSet());
    }
}
