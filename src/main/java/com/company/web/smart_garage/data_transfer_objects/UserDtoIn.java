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

    @Size(min = 2, max = 20, message = FIRST_NAME_INVALID_SIZE)
    private String firstName;
    @Size(min = 2, max = 20, message = LAST_NAME_INVALID_SIZE)
    private String lastName;
    @Size(min = 8, max = 32, message = PASSWORD_LENGTH_INVALID)
    private String password;

    @NotNull(message = EMAIL_IS_REQUIRED)
    @Email(message = USER_EMAIL_INVALID)
    private String email;
    @NotNull(message = PHONE_NUMBER_IS_REQUIRED)
    @Pattern(regexp = PHONE_NUMBER_REGEX, message = PHONE_NUMBER_INVALID)
    private String phoneNumber;
}
