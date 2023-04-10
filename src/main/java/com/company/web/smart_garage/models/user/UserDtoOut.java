package com.company.web.smart_garage.models.user;

import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.models.vehicle.Vehicle;
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
    private Set<Vehicle> vehicles;


}
