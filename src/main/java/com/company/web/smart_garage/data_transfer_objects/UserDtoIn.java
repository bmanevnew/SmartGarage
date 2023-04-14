package com.company.web.smart_garage.data_transfer_objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @Size(min = 2, max = 20, message = USERNAME_INVALID_SIZE_MESSAGE)
    private String firstName;
    @Size(min = 2, max = 20, message = USERNAME_INVALID_SIZE_MESSAGE)
    private String lastName;
    private String password;
    @NotNull(message = EMAIL_IS_REQUIRED)
    @Email(message = EMAIL_INVALID_MESSAGE)
    private String email;
    @NotNull(message = PHONE_NUMBER_IS_REQUIRED)
    @Pattern(regexp = "^\\d{10}$", message = PHONE_NUMBER_INVALID_MESSAGE)
    private String phoneNumber;
}
