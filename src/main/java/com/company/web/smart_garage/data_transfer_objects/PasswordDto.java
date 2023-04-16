package com.company.web.smart_garage.data_transfer_objects;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.company.web.smart_garage.utils.Constants.CONFIRM_PASSWORD_IS_REQUIRED;
import static com.company.web.smart_garage.utils.Constants.PASSWORD_IS_REQUIRED;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDto {

    @NotNull(message = PASSWORD_IS_REQUIRED)
    private String newPassword;
    @NotNull(message = CONFIRM_PASSWORD_IS_REQUIRED)
    private String confirmNewPassword;
}
