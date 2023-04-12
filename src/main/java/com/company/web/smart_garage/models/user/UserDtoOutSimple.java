package com.company.web.smart_garage.models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoOutSimple {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
