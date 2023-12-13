package com.volasoftware.tinder.utility;

import org.apache.commons.lang3.RandomStringUtils;
import org.passay.EnglishCharacterData;

public class PasswordGenerator {
    private static final String SYMBOLS = String.valueOf(EnglishCharacterData.Alphabetical);

    public static String generatePassword() {
        return RandomStringUtils.random(8, SYMBOLS);
    }
}
