package com.company.web.smart_garage.services;

import com.company.web.smart_garage.data_transfer_objects.LoginDto;

public interface AuthService {
    String login(LoginDto loginDto);

    void resetPassword(String email, String path);

    void changePassword(String token, String newPassword);
}
