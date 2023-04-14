package com.company.web.smart_garage.utils.mappers;

import com.company.web.smart_garage.data_transfer_objects.RepairDto;
import com.company.web.smart_garage.models.Repair;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RepairMapper {

    private final ModelMapper modelMapper;

    public Repair dtoToRepair(RepairDto dto) {
        return modelMapper.map(dto, Repair.class);
    }

    public Repair dtoToRepair(RepairDto dto, long id) {
        Repair repair = dtoToRepair(dto);
        repair.setId(id);
        return repair;
    }

    public RepairDto repairToDto(Repair repair) {
        return modelMapper.map(repair, RepairDto.class);
    }
}
