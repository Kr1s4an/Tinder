package com.volasoftware.tinder.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtTokenVerifier {
    public static boolean verifyToken(String token) {
        try {
            Jwts.parser().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

}
