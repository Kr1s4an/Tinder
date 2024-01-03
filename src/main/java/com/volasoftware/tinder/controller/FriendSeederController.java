package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FriendSeederController {

    private final FriendService friendService;

    public FriendSeederController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/api/v1/seed-friends/{id}")
    public ResponseEntity<String> seedFriends(@RequestParam(required = false) Long userId) {
        friendService.seedFriend(userId);

        return ResponseEntity.ok("Friend seeding completed successfully.");
    }
}
