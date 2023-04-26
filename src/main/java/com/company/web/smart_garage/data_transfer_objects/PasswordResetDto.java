package com.company.web.smart_garage.data_transfer_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDto {

    @NotBlank(message = "Please enter your current password.")
    private String currentPassword;

    @NotBlank(message = "Please enter your new password.")
    // @Size(min = 8, message = "Your new password must be at least 8 characters long.")
    private String newPassword;

    @NotBlank(message = "Please confirm your new password.")
    private String confirmNewPassword;


}