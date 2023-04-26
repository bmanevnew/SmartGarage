package com.company.web.smart_garage.data_transfer_objects;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.company.web.smart_garage.utils.Constants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDto {

    @NotBlank(message = CURRENT_PASSWORD_BLANK)
    private String currentPassword;

    @NotBlank(message = NEW_PASSWORD_BLANK)
    private String newPassword;

    @NotBlank(message = CONFIRM_PASSWORD_BLANK)
    private String confirmNewPassword;


}