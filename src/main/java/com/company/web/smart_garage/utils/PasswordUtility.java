package com.company.web.smart_garage.utils;

import org.passay.CharacterData;
import org.passay.*;

import java.util.ArrayList;
import java.util.List;

public class PasswordUtility {

    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 36;
    public static final String INSUFFICIENT_SPECIAL = "INSUFFICIENT_SPECIAL";
    public static final String SPECIAL_SYMBOLS = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    public static String generatePassword() {
        PasswordGenerator gen = new PasswordGenerator();
        return gen.generatePassword(16, getGenerationRules());
    }

    public static boolean validatePassword(String password) {
        PasswordValidator passwordValidator = new PasswordValidator(getValidationRules());
        PasswordData passwordData = new PasswordData(password);
        RuleResult validate = passwordValidator.validate(passwordData);
        return validate.isValid();
    }

    private static List<CharacterRule> getGenerationRules() {
        //should contain 6 lower case english character
        CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase);
        lowerCaseRule.setNumberOfCharacters(6);

        //should contain 6 upper case english character
        CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase);
        upperCaseRule.setNumberOfCharacters(6);

        //should contain 3 digit case english character
        CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit);
        digitRule.setNumberOfCharacters(3);

        //should contain 1 special symbol character
        CharacterRule specialCharacterRule = new CharacterRule(new CharacterData() {
            @Override
            public String getErrorCode() {
                return INSUFFICIENT_SPECIAL;
            }

            @Override
            public String getCharacters() {
                return SPECIAL_SYMBOLS;
            }
        });
        specialCharacterRule.setNumberOfCharacters(1);

        return List.of(lowerCaseRule, upperCaseRule, digitRule, specialCharacterRule);
    }

    private static List<Rule> getValidationRules() {
        List<Rule> rules = new ArrayList<>();

        //should contain 1 lower case english character
        CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase, 1);
        rules.add(lowerCaseRule);

        //should contain 1 upper case english character
        CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase, 1);
        rules.add(upperCaseRule);

        //should contain 1 digit
        CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit, 1);
        rules.add(digitRule);

        //should contain 1 special symbol character
        CharacterRule specialCharacterRule = new CharacterRule(new CharacterData() {
            @Override
            public String getErrorCode() {
                return INSUFFICIENT_SPECIAL;
            }

            @Override
            public String getCharacters() {
                return SPECIAL_SYMBOLS;
            }
        });
        specialCharacterRule.setNumberOfCharacters(1);
        rules.add(specialCharacterRule);

        //should be of appropriate length
        Rule lengthRule = new LengthRule(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH);
        rules.add(lengthRule);

        //should contain only alphabetical english characters,digits and special symbols
        char[] allowedCharacters = (EnglishCharacterData.Alphabetical.getCharacters() +
                EnglishCharacterData.Digit.getCharacters() +
                EnglishCharacterData.Special.getCharacters()).toCharArray();
        Rule validCharactersRule = new AllowedCharacterRule(allowedCharacters);
        rules.add(validCharactersRule);

        return rules;
    }
}