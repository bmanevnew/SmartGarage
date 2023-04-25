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

import static com.company.web.smart_garage.utils.Constants.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthenticationMvcController {

    private final AuthService authService;
    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute(LOGIN_DTO_KEY, new LoginDto());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute(LOGIN_DTO_KEY) LoginDto login,
                        BindingResult bindingResult,
                        HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        String token;
        try {
            token = authService.login(login);
        } catch (AuthenticationException e) {
            bindingResult.rejectValue(PASSWORD_FIELD, AUTH_ERROR, INVALID_LOGIN_ERROR);
            return "login";
        }
        setTokenAsCookie(response, token);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        deleteTokenCookie(response);
        return "redirect:/";
    }

    @GetMapping("/forgot_password")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute(LOGIN_DTO_EMAIL_KEY, new LoginDto());
        return "forgotPassword";
    }

    @PostMapping("/forgot_password")
    public String resetPassword(@ModelAttribute(LOGIN_DTO_EMAIL_KEY) LoginDto loginDto,
                                Model model) {
        String email = loginDto.getUsernameOrEmail();
        String response = SUCCESSFUL_RESET;
        try {
            authService.resetPassword(email, CHANGE_PASSWORD_ENDPOINT);
        } catch (EntityNotFoundException | InvalidParamException e) {
            response = String.format(USER_WITH_EMAIL_S_DOES_NOT_EXIST, email);
        }
        model.addAttribute("response", response);
        return "forgotPassword";
    }

    @GetMapping("/change_password")
    public String getChangePasswordPage(@RequestParam(name = TOKEN_KEY) String token,
                                        Model model) {
        model.addAttribute(PASSWORD_DTO_KEY, new PasswordDto());
        model.addAttribute(TOKEN_KEY, token);
        return "updatePassword";
    }

    @PostMapping("/change_password")
    public String changePassword(@RequestParam(name = TOKEN_KEY) String token,
                                 @Valid @ModelAttribute("passwordDto") PasswordDto passwordDto,
                                 BindingResult bindingResult,
                                 Model model) {
        model.addAttribute(TOKEN_KEY, token);
        if (bindingResult.hasErrors()) {
            return "updatePassword";
        }
        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
            bindingResult.rejectValue(CONFIRM_PASSWORD_FIELD, PASSWORD_ERROR, PASSWORD_DOES_NOT_MATCH);
            return "updatePassword";
        }
        try {
            authService.changePassword(token, passwordDto.getNewPassword());
        } catch (EntityNotFoundException | InvalidParamException e) {
            bindingResult.rejectValue(CONFIRM_PASSWORD_FIELD, PASSWORD_ERROR, e.getMessage());
            return "updatePassword";
        }
        return "redirect:/auth/login";
    }


    private void setTokenAsCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        //convert milliseconds to seconds
        cookie.setMaxAge((int) jwtExpirationDate / 1000);
        cookie.setPath(ROOT_PATH);
        cookie.setAttribute(SAME_SITE, SAME_SITE_LAX);

        response.addCookie(cookie);
    }

    private void deleteTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        cookie.setPath(ROOT_PATH);
        cookie.setAttribute(SAME_SITE, SAME_SITE_LAX);

        response.addCookie(cookie);
    }
}
