package com.company.web.smart_garage.services;

import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.PasswordResetToken;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.repositories.PasswordResetTokenRepository;
import com.company.web.smart_garage.services.impl.PasswordResetTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordResetTokenServiceTests {

    @Mock
    private PasswordResetTokenRepository prtRepository;

    @InjectMocks
    private PasswordResetTokenServiceImpl passwordResetTokenService;

    @Mock
    private User user;
    private PasswordResetToken token;

    @BeforeEach
    public void beforeEach() {

        token = new PasswordResetToken();
        token.setId(1L);
        token.setToken("token");
        token.setExpiryDate(new Date(new Date().getTime() + 360000));
    }

    @Test
    public void createPasswordResetToken_Should_CreateTokenAndDeleteOldToken() {
        when(user.getPassResetToken()).thenReturn(token);
        when(prtRepository.existsByToken(anyString())).thenReturn(false);
        when(prtRepository.save(any(PasswordResetToken.class))).thenReturn(token);

        String newToken = passwordResetTokenService.createPasswordResetTokenForUser(user);

        verify(prtRepository).deleteById(1L);
        verify(prtRepository).save(any(PasswordResetToken.class));
        assertNotNull(newToken);
    }

    @Test
    public void getPasswordResetToken_Should_ReturnToken_When_TokenIsValid() {
        when(prtRepository.findByToken(anyString())).thenReturn(Optional.of(token));

        PasswordResetToken returnedToken = passwordResetTokenService.getPasswordResetToken(token.getToken());

        assertEquals(token, returnedToken);
    }

    @Test
    public void getPasswordResetToken_Should_ThrowException_When_TokenDoesNotExist() {
        when(prtRepository.findByToken(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> passwordResetTokenService.getPasswordResetToken(token.getToken()));
    }

    @Test
    public void getPasswordResetToken_Should_ThrowException_When_TokenIsExpired() {
        token.setExpiryDate(new Date(0));
        when(prtRepository.findByToken(anyString())).thenReturn(Optional.of(token));

        assertThrows(InvalidParamException.class, () -> passwordResetTokenService.getPasswordResetToken(token.getToken()));
    }

    @Test
    public void deletePasswordResetToken_Should_DeletePasswordResetToken_When_ParameterValid() {
        when(prtRepository.existsById(anyLong())).thenReturn(true);

        passwordResetTokenService.deletePasswordResetToken(1);

        verify(prtRepository).deleteById(1L);
    }

    @Test
    public void deletePasswordResetToken_Should_ThrowException_When_IdIsNegativeOrZero() {
        assertThrows(InvalidParamException.class, () -> passwordResetTokenService.deletePasswordResetToken(-5));
    }

    @Test
    public void deletePasswordResetToken_Should_ThrowException_When_TokenDoesNotExist() {
        when(prtRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> passwordResetTokenService.deletePasswordResetToken(1));
    }
}
