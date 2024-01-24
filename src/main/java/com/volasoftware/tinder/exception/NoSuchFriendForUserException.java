package com.volasoftware.tinder.exception;

public class NoSuchFriendForUserException extends RuntimeException {
    public NoSuchFriendForUserException(String message) {
        super(message);
    }
}
