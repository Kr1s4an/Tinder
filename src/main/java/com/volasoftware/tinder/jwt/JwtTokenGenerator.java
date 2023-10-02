package com.volasoftware.tinder.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenGenerator {
    private static final String SECRET_KEY = "UnbreakableSecretKey";
    public static final long EXPIRATION_TIME =604800000L; //7 days

    public static String generateToken(String username){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

    }
}
