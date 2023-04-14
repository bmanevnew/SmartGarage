package com.company.web.smart_garage.models.visit;

import com.company.web.smart_garage.models.repair.RepairDto;
import com.company.web.smart_garage.models.user.UserDtoOutSimple;
import com.company.web.smart_garage.models.vehicle.VehicleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisitDtoOut {

    private VehicleDto vehicleDto;

    private UserDtoOutSimple userDtoOut;

    private Set<RepairDto> repairs;

    private LocalDate date;
}