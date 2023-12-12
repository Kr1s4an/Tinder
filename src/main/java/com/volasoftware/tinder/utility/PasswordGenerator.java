package com.volasoftware.tinder.utility;

import org.apache.commons.lang3.RandomStringUtils;

public class PasswordGenerator {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";

    public static String generatePassword() {
        return RandomStringUtils.random(8, CHARACTERS);
    }
}
