package com.company.web.smart_garage.utils;

import com.company.web.smart_garage.models.User;
import org.springframework.security.core.Authentication;

public class AuthorizationUtils {


    public static boolean userIsAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(sga -> sga.getAuthority().equals("ROLE_ADMIN"));
    }

    public static boolean userIsEmployee(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(sga -> sga.getAuthority().equals("ROLE_EMPLOYEE"));
    }

    public static boolean userIsAdminOrEmployee(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(sga -> sga.getAuthority().equals("ROLE_EMPLOYEE") ||
                        sga.getAuthority().equals("ROLE_ADMIN"));
    }

    //TODO to be deleted after proper authentication in user classes
    public static boolean userIsAdmin(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }

    public static boolean userIsEmployee(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_EMPLOYEE"));
    }

    public static boolean userIsDeleted(User user) {
        //deleted role is now non existent, this is just for testing purposes
        return false;
    }
}
