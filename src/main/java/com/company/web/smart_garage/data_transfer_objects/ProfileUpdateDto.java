package com.company.web.smart_garage.data_transfer_objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateDto {

    @NotEmpty(message = "First name can not be empty.")
    @Size(min = 4, max = 32, message = "First name should be between 4 and 32 symbols.")
    private String firstName;

    @NotEmpty(message = "Last name can not be empty.")
    @Size(min = 4, max = 32, message = "Last name should be between 4 and 32 symbols.")
    private String lastName;

//    @NotEmpty(message = "Email can not be empty.")
//    @Email(message = "Email should be a valid email.")
//    private String email;

    //      @NotEmpty(message = "Old password can not be empty.")
//    private String newPassword;
//
//     @NotEmpty(message = "Old password can not be empty.")
//    private String oldPassword;
//     @NotEmpty(message = "Confirm old password can not be empty.")
//    private String oldPasswordConfirm;
    @NotEmpty(message = "Phone number can not be empty.")
    @Size(min = 10, max = 10, message = "Phone Number should be 10 symbols.")
    private String phoneNumber;

}