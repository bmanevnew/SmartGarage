package com.company.web.smart_garage.data_transfer_objects;

import jakarta.validation.constraints.NotNull;
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
public class LoginDto {
    @NotNull(message = USERNAME_IS_REQUIRED)
    @Size(min = 2, max = 20, message = USERNAME_INVALID_SIZE_MESSAGE)
    private String usernameOrEmail;

    @NotNull(message = PASSWORD_IS_REQUIRED)
    private String password;
}
