package com.company.web.smart_garage.data_transfer_objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ProfileUpdateDto {

    @NotEmpty(message = "First name can not be empty.")
    @Size(min = 4, max = 32, message = "First name should be between 4 and 32 symbols.")
    private String firstName;

    @NotEmpty(message = "Last name can not be empty.")
    @Size(min = 4, max = 32, message = "Last name should be between 4 and 32 symbols.")
    private String lastName;

    //  @NotEmpty(message = "Email can not be empty.")
    @Email(message = "Email should be a valid email.")
    private String email;

    //  @NotEmpty(message = "Old password can not be empty.")
    private String newPassword;

    // @NotEmpty(message = "Old password can not be empty.")
    private String oldPassword;
    //  @NotEmpty(message = "Confirm old password can not be empty.")
    private String oldPasswordConfirm;

    private String phoneNumber;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getOldPasswordConfirm() {
        return oldPasswordConfirm;
    }

    public void setOldPasswordConfirm(String oldPasswordConfirm) {
        this.oldPasswordConfirm = oldPasswordConfirm;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
