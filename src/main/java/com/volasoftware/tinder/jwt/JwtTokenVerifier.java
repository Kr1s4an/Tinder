package com.volasoftware.tinder.jwt;

import com.volasoftware.tinder.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.HashMap;
import java.util.Map;


public class JwtTokenVerifier {
    public static boolean verifyToken(String token) {
        try {
            Jwts.parser().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Map<String, String> getUserFromToken(String token) {
        Claims claims = Jwts.parser().parseClaimsJws(token).getBody();
        Map<String, String> user = new HashMap<>();
        user.put("username", claims.getSubject());
        user.put( "email", claims.get("email", String.class));
        return user;
    }

}
