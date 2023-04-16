package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.PasswordResetToken;
import com.company.web.smart_garage.models.User;

public interface PasswordResetTokenService {

    PasswordResetToken getPasswordResetToken(String token);

    String createPasswordResetTokenForUser(User user);

    void deletePasswordResetToken(long id);
}
