package com.company.web.smart_garage.controllers.rest;

import com.company.web.smart_garage.data_transfer_objects.JwtAuthResponse;
import com.company.web.smart_garage.data_transfer_objects.LoginDto;
import com.company.web.smart_garage.data_transfer_objects.PasswordDto;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.services.AuthService;
import com.company.web.smart_garage.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.company.web.smart_garage.utils.Constants.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public static final String ENDPOINT_PATH = "/api/auth/change_password";
    private final AuthService authService;
    private final UserService userService;

    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);
        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/reset_password")
    public ResponseEntity<String> resetPassword(@RequestParam("email") String email) {
        authService.resetPassword(email, ENDPOINT_PATH);
        return ResponseEntity.ok(SUCCESSFUL_RESET);
    }

    @PostMapping("/change_password")
    public ResponseEntity<String> changePassword(@RequestParam(name = "token") String token,
                                                 @Valid @RequestBody PasswordDto passwordDto) {
        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
            throw new InvalidParamException(PASSWORD_DOES_NOT_MATCH);
        }
        authService.changePassword(token, passwordDto.getNewPassword());
        return ResponseEntity.ok(PASSWORD_SUCCESSFULLY_CHANGED);
    }

    @PostMapping("/change_password_auth")
    public ResponseEntity<String> changePasswordAuth(@Valid @RequestBody PasswordDto passwordDto,
                                                     Authentication authentication) {
        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
            throw new InvalidParamException(PASSWORD_DOES_NOT_MATCH);
        }
        User user = userService.getByUsernameOrEmail(authentication.getName());
        user.setPassword(passwordDto.getNewPassword());
        userService.update(user);
        return ResponseEntity.ok(PASSWORD_SUCCESSFULLY_CHANGED);
    }
}
