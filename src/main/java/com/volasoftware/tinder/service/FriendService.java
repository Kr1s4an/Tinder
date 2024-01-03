package com.volasoftware.tinder.service;

import org.springframework.web.bind.annotation.RequestParam;

public interface FriendService {
    void seedFriend(@RequestParam(required = false) Long userId);
}
