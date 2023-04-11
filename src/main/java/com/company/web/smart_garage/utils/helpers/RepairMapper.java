package com.company.web.smart_garage.utils.helpers;

import com.company.web.smart_garage.models.repair.Repair;
import com.company.web.smart_garage.models.repair.RepairDto;
import org.springframework.stereotype.Component;

@Component
public class RepairMapper {

    public Repair dtoToRepair(RepairDto dto) {
        Repair repair = new Repair();
        repair.setName(dto.getName());
        repair.setPrice(dto.getPrice());
        return repair;
    }

    public Repair dtoToRepair(RepairDto dto, long id) {
        Repair repair = dtoToRepair(dto);
        repair.setId(id);
        return repair;
    }

    public RepairDto repairToDto(Repair repair) {
        RepairDto dto = new RepairDto();
        dto.setName(repair.getName());
        dto.setPrice(repair.getPrice());
        return dto;
    }
}
