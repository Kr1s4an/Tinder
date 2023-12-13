package com.volasoftware.tinder.utility;

import org.apache.commons.lang3.RandomStringUtils;
import org.passay.EnglishCharacterData;

public class PasswordGenerator {
    private static final String SYMBOLS = String.valueOf(EnglishCharacterData.Alphabetical.getCharacters());
    private static final String DIGIT = String.valueOf(EnglishCharacterData.Digit.getCharacters());

    public static String generatePassword() {
        return RandomStringUtils.random(8, SYMBOLS + DIGIT);
    }
}
