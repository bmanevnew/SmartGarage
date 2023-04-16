package com.company.web.smart_garage.controllers;

import com.company.web.smart_garage.data_transfer_objects.JwtAuthResponse;
import com.company.web.smart_garage.data_transfer_objects.LoginDto;
import com.company.web.smart_garage.data_transfer_objects.PasswordDto;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);
        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/reset_password")
    public ResponseEntity<String> resetPassword(@RequestParam("email") String email) {
        authService.resetPassword(email, "/api/auth/change_password");
        return ResponseEntity.ok("Password reset email successfully sent.");
    }

    @PostMapping("/change_password")
    public ResponseEntity<String> changePassword(@RequestParam(name = "token") String token,
                                                 @Valid @RequestBody PasswordDto passwordDto) {
        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
            throw new InvalidParamException("Password confirmation does not match.");
        }
        authService.changePassword(token, passwordDto.getNewPassword());
        return ResponseEntity.ok("Successfully changed your password.");
    }
}
