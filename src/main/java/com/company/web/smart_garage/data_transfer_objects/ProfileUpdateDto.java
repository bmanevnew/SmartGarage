package com.company.web.smart_garage.data_transfer_objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
public class ProfileUpdateDto {

    @NotEmpty(message = FIRST_NAME_EMPTY)
    @Size(min = 4, max = 32, message = FIRST_NAME_LENGTH_INVALID)
    private String firstName;

    @NotEmpty(message = LAST_NAME_EMPTY)
    @Size(min = 4, max = 32, message = LAST_NAME_LENGTH_INVALID)
    private String lastName;

    @NotEmpty(message = EMAIL_EMPTY)
    @Email(message = EMAIL_INVALID)
    private String email;

    @NotEmpty(message = PHONE_NUMBER_EMPTY)
    @Size(min = 10, max = 10, message = PHONE_NUMBER_LENGTH_INVALID)
    private String phoneNumber;

}