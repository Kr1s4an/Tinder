package com.volasoftware.tinder.utility;

import org.apache.commons.lang3.RandomStringUtils;
import org.passay.EnglishCharacterData;

public class NumberGenerator {
    private static final String DIGIT = String.valueOf(EnglishCharacterData.Digit.getCharacters());

    public static String generateNumber() {
        return RandomStringUtils.random(8, DIGIT);
    }
}
