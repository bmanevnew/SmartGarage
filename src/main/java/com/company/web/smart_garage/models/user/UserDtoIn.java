package com.company.web.smart_garage.models.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDtoIn {

    @NotBlank
    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 symbols.")
    private String username;
    @NotBlank
    @Email(message = "Email must be a valid email.")
    private String email;
    @NotBlank
    @Size(min = 10, max = 10, message = "Phone number must be a valid phone number.")
    private String phoneNumber;
}
