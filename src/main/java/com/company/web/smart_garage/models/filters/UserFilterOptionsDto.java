package com.company.web.smart_garage.models.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterOptionsDto {
    private Long id;
    private String firstName;
    private String email;
    private String username;
    private String phoneNumber;
    private String brand;
    private String model;
    private String dateFrom;
    private String dateTo;
    private String sort;
}
