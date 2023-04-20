package com.company.web.smart_garage.data_transfer_objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class ContactUsDto {

    @NotBlank(message = REQUIRED_FIELD)
    private String name;
    @NotBlank(message = REQUIRED_FIELD)
    @Email
    private String email;
    @Size(min = 2, max = 32, message = SUBJECT_INVALID_SIZE)
    private String subject;
    @Size(min = 2, max = 1000, message = MESSAGE_INVALID_SIZE)
    private String message;
}
