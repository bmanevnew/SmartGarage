package com.company.web.smart_garage.utils.helpers;

import com.company.web.smart_garage.models.repair.Repair;
import com.company.web.smart_garage.models.repair.RepairDto;
import com.company.web.smart_garage.models.visit.Visit;
import com.company.web.smart_garage.models.visit.VisitDtoIn;
import com.company.web.smart_garage.models.visit.VisitDtoOut;
import com.company.web.smart_garage.models.visit.VisitDtoOutSimple;
import com.company.web.smart_garage.services.RepairService;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.services.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class VisitMapper {

    private final UserService userService;
    private final VehicleService vehicleService;
    private final RepairService repairService;
    private final VehicleMapper vehicleMapper;
    private final UserMapper userMapper;
    private final RepairMapper repairMapper;

    public VisitDtoOut visitToDto(Visit visit) {
        VisitDtoOut dto = new VisitDtoOut();
        dto.setVehicleDto(vehicleMapper.vehicleToDto(visit.getVehicle()));
        dto.setUserDtoOut(userMapper.ObjectToDtoSimple(visit.getVisitor()));
        Set<RepairDto> repairDtos = visit.getRepairs().stream()
                .map(repairMapper::repairToDto)
                .collect(Collectors.toSet());
        dto.setRepairs(repairDtos);
        dto.setDate(visit.getDate());
        return dto;
    }

    public VisitDtoOut visitToDtoWOVisitor(Visit visit) {
        VisitDtoOut dto = new VisitDtoOut();
        Set<RepairDto> repairDtos = visit.getRepairs().stream()
                .map(repairMapper::repairToDto)
                .collect(Collectors.toSet());
        dto.setRepairs(repairDtos);
        dto.setDate(visit.getDate());
        return dto;
    }

    public Visit dtoToVisit(VisitDtoIn visitDto) {
        Visit visit = new Visit();
        visit.setVisitor(userService.getUserById(visitDto.getUserId()));
        visit.setVehicle(vehicleService.getById(visitDto.getVehicleId()));
        visit.setRepairs(getRepairsFromIds(visitDto.getRepairIds()));
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
        dto.setDate(visit.getDate());
        dto.setGetTotalCost(visit.getTotalCost());
        return dto;
    }
}
