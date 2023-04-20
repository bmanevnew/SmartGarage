package com.company.web.smart_garage.data_transfer_objects;

import jakarta.validation.constraints.Email;
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
public class ContactUsDto {

    @NotBlank(message = "Required field.")
    private String name;
    @NotBlank(message = "Required field.")
    @Email
    private String email;
    @Size(min = 2, max = 32, message = "Must be between 2 and 32 symbols.")
    private String subject;
    @Size(min = 2, max = 1000, message = "Must be between 2 and 1000 symbols.")
    private String message;
}
