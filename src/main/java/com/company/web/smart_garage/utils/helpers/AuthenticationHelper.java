package com.company.web.smart_garage.utils.helpers;


import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.UnauthorizedOperationException;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import static com.company.web.smart_garage.utils.Constants.INVALID_AUTHENTICATION_ERROR;
import static com.company.web.smart_garage.utils.Constants.INVALID_LOGIN_ERROR;
import static org.apache.tomcat.websocket.Constants.AUTHORIZATION_HEADER_NAME;


@Component
public class AuthenticationHelper {


    private final UserService userService;

    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(AUTHORIZATION_HEADER_NAME)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The requested resource requires authentication");
        }
        try {
            String userInfo = headers.getFirst(AUTHORIZATION_HEADER_NAME);
            String password = getPassword(userInfo);
            String username = getUsername(userInfo);
            User user = userService.getByUsername(username);

            if (!user.getPassword().equals(password)) {
                throw new UnauthorizedOperationException(INVALID_AUTHENTICATION_ERROR);
            }
            return userService.getByUsername(username);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username");
        }
    }

    public User tryGetCurrentUser(HttpSession session) {
        String currentUsername = (String) session.getAttribute("currentUser");

        if (currentUsername == null) {
            throw new UnauthorizedOperationException(INVALID_AUTHENTICATION_ERROR);
        }

        return userService.getByUsername(currentUsername);
    }

    public User verifyAuthentication(String username, String password) {
        try {
            User user = userService.getByUsername(username);
            if (!user.getPassword().equals(password)) {
                throw new UnauthorizedOperationException(INVALID_LOGIN_ERROR);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new UnauthorizedOperationException(INVALID_LOGIN_ERROR);
        }
    }

//    public void checkIfBlocked(User user) {
//        List<Role> roles = user.getRoles();
//        try {
//        if (roles.stream().anyMatch(t -> t.getRoleName().equals("blocked"))) {
//            throw new UnauthorizedOperationException(
//                    format("Your account is blocked"));
//        }
//        } catch (UnauthorizedOperationException e) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Blocked account");
//        }
//    }

    private String getUsername(String userInfo) {
        int firstSpace = userInfo.indexOf(" ");
        if (firstSpace == -1) {
            throw new UnauthorizedOperationException(INVALID_AUTHENTICATION_ERROR);
        }

        return userInfo.substring(0, firstSpace);
    }

    private String getPassword(String userInfo) {
        int firstSpace = userInfo.indexOf(" ");
        if (firstSpace == -1) {
            throw new UnauthorizedOperationException(INVALID_AUTHENTICATION_ERROR);
        }

        return userInfo.substring(firstSpace + 1);
    }


}
