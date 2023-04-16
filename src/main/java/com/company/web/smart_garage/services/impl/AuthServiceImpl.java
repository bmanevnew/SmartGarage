package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.data_transfer_objects.LoginDto;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.PasswordResetToken;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.security.JwtTokenProvider;
import com.company.web.smart_garage.services.AuthService;
import com.company.web.smart_garage.services.EmailSenderService;
import com.company.web.smart_garage.services.PasswordResetTokenService;
import com.company.web.smart_garage.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.company.web.smart_garage.utils.PasswordUtility.validatePassword;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenService prtService;

    @Override
    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public void resetPassword(String email, String path) {
        User user = userService.getByEmail(email);
        String token = prtService.createPasswordResetTokenForUser(user);

        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath();
        builder.path(path);
        builder.queryParam("token", token);
        String url = builder.toUriString();

        emailSenderService.sendEmail(email, "Password reset for Smart Garage.",
                "Dear " + user.getUsername() + ",\n\n" +
                        "Use the following url to reset your password: " + url + "\n\n" +
                        "Best regards,\n" +
                        "The Smart Garage Team");
    }

    @Override
    public void changePassword(String token, String newPassword) {
        if (!validatePassword(newPassword)) throw new InvalidParamException("Password too weak.");
        PasswordResetToken resetToken = prtService.getPasswordResetToken(token);
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.update(user.getId(), user, user);
        prtService.deletePasswordResetToken(resetToken.getId());
    }
}
