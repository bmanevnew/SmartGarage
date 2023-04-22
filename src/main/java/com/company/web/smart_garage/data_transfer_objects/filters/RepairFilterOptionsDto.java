package com.company.web.smart_garage.data_transfer_objects.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepairFilterOptionsDto {

    private String name;
    private String priceFrom;
    private String priceTo;
    private String sort;
}
