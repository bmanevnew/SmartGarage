package com.company.web.smart_garage.models.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.company.web.smart_garage.utils.Constants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoIn {

    //TODO username will eventually be generated and will be removed from dto
    @NotBlank
    @Size(min = 2, max = 20, message = USERNAME_INVALID_SIZE_MESSAGE)
    private String username;
    @NotBlank
    @Email(message = EMAIL_INVALID_MESSAGE)
    private String email;
    @NotBlank
    @Size(min = 10, max = 10, message = PHONE_NUMBER_INVALID_MESSAGE)
    private String phoneNumber;
}
