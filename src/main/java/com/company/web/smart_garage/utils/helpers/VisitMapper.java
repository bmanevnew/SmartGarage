package com.company.web.smart_garage.utils.helpers;

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
        dto.setUserDtoOut(modelMapper.map(visit.getVisitor(), UserDtoOutSimple.class));
        dto.setRepairs(visit.getRepairs().stream()
                .map(repair -> modelMapper.map(repair, RepairDto.class))
                .collect(Collectors.toSet()));
        dto.setDate(visit.getDate().toLocalDate());
        return dto;
    }

    public VisitDtoOut visitToDtoWOVisitor(Visit visit) {
        VisitDtoOut dto = new VisitDtoOut();
        dto.setDate(visit.getDate().toLocalDate());
        return dto;
    }

    public Visit dtoToVisit(VisitDtoIn visitDto) {
        Visit visit = new Visit();
        visit.setVisitor(userService.getUserById(visitDto.getUserId()));
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

    public VisitDtoOutSimple visitDtoOutSimple(Visit visit) {
        VisitDtoOutSimple dto = new VisitDtoOutSimple();
        dto.setLicensePlate(visit.getVehicle().getLicensePlate());
        dto.setVin(visit.getVehicle().getVin());
        Set<RepairDto> repairDtos = visit.getRepairs().stream()
                .map(repair -> new RepairDto(repair.getName(), repair.getPrice()))
                .collect(Collectors.toSet());
        dto.setRepairs(repairDtos);
        dto.setDate(visit.getDate().toLocalDate());
        dto.setGetTotalCost(visit.getTotalCost());
        return dto;
    }
}
