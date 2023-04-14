package com.company.web.smart_garage.data_transfer_objects;

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
public class VisitDtoOutSimple {
    private String licensePlate;
    private String vin;
    private Set<RepairDto> repairs;
    private LocalDate date;
    private double getTotalCost;
}
