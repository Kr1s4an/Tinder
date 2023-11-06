package com.volasoftware.tinder.exception;

public class PasswordDoesNotMatchException extends RuntimeException{
    public PasswordDoesNotMatchException(String message) {
        super(message);
    }
}
