package com.company.web.smart_garage.utils;

import com.company.web.smart_garage.exceptions.UnauthorizedOperationException;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static com.company.web.smart_garage.utils.Constants.*;

@Component
public class AuthorizationUtils {

    private final UserService userService;

    public AuthorizationUtils(UserService userService) {
        this.userService = userService;
    }

    public static boolean userIsAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(sga -> sga.getAuthority().equals(ROLE_ADMIN));
    }

    public static boolean userIsEmployee(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(sga -> sga.getAuthority().equals(ROLE_EMPLOYEE));
    }

    public static boolean userIsAdminOrEmployee(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(sga -> sga.getAuthority().equals(ROLE_EMPLOYEE) ||
                        sga.getAuthority().equals(ROLE_ADMIN));
    }

    public static boolean userIsAdmin(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals(ROLE_ADMIN));
    }

    public static boolean userIsEmployee(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals(ROLE_EMPLOYEE));
    }

    public User tryGetCurrentUser(Authentication authentication) {
        String currUsernameOrEmail = authentication.getName();

        if (currUsernameOrEmail == null) {
            throw new UnauthorizedOperationException(INVALID_LOGIN_ERROR);
        }

        return userService.getByUsernameOrEmail(currUsernameOrEmail);
    }
}
