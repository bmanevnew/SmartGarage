package com.company.web.smart_garage.controllers.mvc;

import com.company.web.smart_garage.data_transfer_objects.LoginDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
