package com.company.web.smart_garage.services;

import com.company.web.smart_garage.data_transfer_objects.LoginDto;

public interface AuthService {
    String login(LoginDto loginDto);
}
