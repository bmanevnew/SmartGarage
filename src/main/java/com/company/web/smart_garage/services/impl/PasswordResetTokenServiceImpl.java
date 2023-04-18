package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.exceptions.APIException;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.PasswordResetToken;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.repositories.PasswordResetTokenRepository;
import com.company.web.smart_garage.services.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

import static com.company.web.smart_garage.utils.Constants.*;

@RequiredArgsConstructor
@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository prtRepository;
    @Value("${password-reset-token-expiration-milliseconds}")
    private long prtExpirationTime;

    @Override
    public String createPasswordResetTokenForUser(User user) {
        if (user.getPassResetToken() != null) {
            prtRepository.deleteById(user.getPassResetToken().getId());
        }
        PasswordResetToken tokenObj = new PasswordResetToken();
        tokenObj.setUser(user);
        tokenObj.setExpiryDate(new Date(new Date().getTime() + prtExpirationTime));
        String token;
        String tokenHash;
        do {
            token = UUID.randomUUID().toString();
            tokenHash = hashToken(token);
        } while (prtRepository.existsByToken(tokenHash));

        tokenObj.setToken(tokenHash);
        prtRepository.save(tokenObj);
        return token;
    }

    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        String tokenHash = hashToken(token);
        PasswordResetToken passToken = prtRepository.findByToken(tokenHash)
                .orElseThrow(() -> new EntityNotFoundException("Password reset token", "token", token));
        if (isTokenExpired(passToken)) throw new InvalidParamException(TOKEN_EXPIRED);
        return passToken;
    }

    private String hashToken(String token) {
        try {
            MessageDigest encoder = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = encoder.digest(token.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new APIException(HASH_ALGORITHM_NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public void deletePasswordResetToken(long id) {
        if (id <= 0) throw new InvalidParamException(ID_MUST_BE_POSITIVE);
        if (!prtRepository.existsById(id)) throw new EntityNotFoundException("Token", id);
        prtRepository.deleteById(id);
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        Date currDate = new Date();
        return passToken.getExpiryDate().before(currDate);
    }
}
