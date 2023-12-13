package com.volasoftware.tinder.utility;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {
    static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public static String encodePassword(String password){
        return passwordEncoder.encode(password);
    }
    public static boolean equals(String rawPassword, String encodedPassword){
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
