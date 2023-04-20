package com.company.web.smart_garage.data_transfer_objects;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.company.web.smart_garage.utils.Constants.PASSWORD_IS_REQUIRED;
import static com.company.web.smart_garage.utils.Constants.USERNAME_IS_REQUIRED;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    @NotBlank(message = USERNAME_IS_REQUIRED)
    private String usernameOrEmail;

    @NotBlank(message = PASSWORD_IS_REQUIRED)
    private String password;
}
