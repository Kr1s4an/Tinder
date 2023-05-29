package com.volasoftware.tinder.exception;

public class InvalidVerificationToken extends RuntimeException{

    public InvalidVerificationToken(String message) {
        super(message);
    }
}
