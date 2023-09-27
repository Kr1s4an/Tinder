package com.volasoftware.tinder.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.HttpCookie;

public class JwtTokenUtils {


    public static void setTokenCookie(HttpServletResponse response, String token){
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setMaxAge((int) (JwtTokenGenerator.EXPIRATION_TIME / 1000));
        response.addCookie(cookie);
    }

    public static String getTokenFromRequest(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies!= null) {
            for (Cookie cookie : cookies){
                if (cookie.getName().equals("token")){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
