package com.volasoftware.tinder.exception;

import com.volasoftware.tinder.model.FriendDetails;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class NoFriendsFoundException extends RuntimeException {
    public NoFriendsFoundException(String message) {
        super(message);
    }
}
