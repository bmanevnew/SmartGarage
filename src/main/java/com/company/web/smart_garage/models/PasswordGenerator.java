package com.company.web.smart_garage.models;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordGenerator {
    private static final String
            CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+=-";
    private static final Random RANDOM = new SecureRandom();
    private static final int PASSWORD_LENGTH = 8;

    public static String generatePassword() {
        StringBuilder passwordBuilder = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            passwordBuilder.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return passwordBuilder.toString();
    }
}