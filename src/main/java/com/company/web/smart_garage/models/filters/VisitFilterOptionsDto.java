package com.company.web.smart_garage.models.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisitFilterOptionsDto {

    private String visitor;
    private String vehicle;
    private String dateFrom;
    private String dateTo;
    private String sort;
}
