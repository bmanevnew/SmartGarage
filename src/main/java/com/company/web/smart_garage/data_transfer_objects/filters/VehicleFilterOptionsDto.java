package com.company.web.smart_garage.data_transfer_objects.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleFilterOptionsDto {

    private String owner;
    private String model;
    private String brand;
    private String prodYearFrom;
    private String prodYearTo;
    private String sort;
}
