package com.company.web.smart_garage.controllers.mvc;

import com.company.web.smart_garage.data_transfer_objects.LoginDto;
import com.company.web.smart_garage.data_transfer_objects.PasswordDto;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.company.web.smart_garage.utils.Constants.INVALID_LOGIN_ERROR;
import static com.company.web.smart_garage.utils.Constants.JWT_COOKIE_NAME;

@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthenticationMvcController {

    private final AuthService authService;
    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new LoginDto());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("login") LoginDto login,
                        BindingResult bindingResult,
                        HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        String token;
        try {
            token = authService.login(login);
        } catch (AuthenticationException e) {
            bindingResult.rejectValue("password", "auth_error", INVALID_LOGIN_ERROR);
            return "login";
        }
        setTokenAsCookie(response, token);
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        deleteTokenCookie(response);
        return "redirect:/";
    }

    @GetMapping("/forgot_password")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute("loginEmail", new LoginDto());
        return "forgotPassword";
    }

    @PostMapping("/forgot_password")
    public String resetPassword(@ModelAttribute("loginEmail") LoginDto loginDto,
                                Model model) {
        String email = loginDto.getUsernameOrEmail();
        String response = "Successfully sent reset link.";
        try {
            authService.resetPassword(email, "/auth/change_password");
        } catch (EntityNotFoundException e) {
            response = String.format("User with email %s does not exist.", email);
        }
        model.addAttribute("response", response);
        return "forgotPassword";
    }

    @GetMapping("/change_password")
    public String getChangePasswordPage(@RequestParam(name = "token") String token,
                                        Model model) {
        model.addAttribute("passwordDto", new PasswordDto());
        model.addAttribute("token", token);
        return "updatePassword";
    }

    @PostMapping("/change_password")
    public String changePassword(@RequestParam(name = "token") String token,
                                 @Valid @ModelAttribute("passwordDto") PasswordDto passwordDto,
                                 BindingResult bindingResult,
                                 Model model) {
        model.addAttribute("token", token);
        if (bindingResult.hasErrors()) {
            return "updatePassword";
        }
        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "password_error",
                    "Password confirmation does not match.");
            return "updatePassword";
        }
        try {
            authService.changePassword(token, passwordDto.getNewPassword());
        } catch (EntityNotFoundException | InvalidParamException e) {
            bindingResult.rejectValue("confirmNewPassword", "password_error", e.getMessage());
            return "updatePassword";
        }
        return "redirect:/auth/login";
    }


    private void setTokenAsCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge((int) jwtExpirationDate / 1000);

        response.addCookie(cookie);
    }

    private void deleteTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}
