package com.company.web.smart_garage.data_transfer_objects;

import com.company.web.smart_garage.models.Role;
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
    private Set<Role> roles;
    private Set<VehicleDto> vehicles;
    private Set<VisitDtoOut> visits;
}
