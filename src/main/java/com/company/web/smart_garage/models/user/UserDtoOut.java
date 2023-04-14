package com.company.web.smart_garage.models.user;

import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.models.vehicle.VehicleDto;
import com.company.web.smart_garage.models.visit.Visit;
import com.company.web.smart_garage.models.visit.VisitDtoOut;
import com.company.web.smart_garage.models.visit.VisitDtoOutSimple;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoOut {

    private long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private Set<Role> roles;
    private Set<VehicleDto> vehicles;
    private Set<VisitDtoOutSimple> visits;
}
