package com.company.web.smart_garage.models.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.company.web.smart_garage.utils.Constants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoIn {

    //TODO username will eventually be generated and will be removed from dto
    @NotBlank
    @Size(min = 2, max = 20, message = USERNAME_INVALID_SIZE_MESSAGE)
    private String username;
   // @NotBlank
    @Size(min = 2, max = 20, message = USERNAME_INVALID_SIZE_MESSAGE)
    private String firstName;
   // @NotBlank
    @Size(min = 2, max = 20, message = USERNAME_INVALID_SIZE_MESSAGE)
    private String lastName;
    @NotBlank
    @Email(message = EMAIL_INVALID_MESSAGE)
    private String email;
    @NotBlank
    @Size(min = 10, max = 10, message = PHONE_NUMBER_INVALID_MESSAGE)
    private String phoneNumber;

}
