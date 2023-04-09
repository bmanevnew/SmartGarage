package com.company.web.smart_garage.models.user;

import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.models.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
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
