package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.dto.FriendSearchDto;
import com.volasoftware.tinder.model.FriendDetails;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.service.FriendService;
import com.volasoftware.tinder.service.UserService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    public FriendController(FriendService friendService,
                            UserService userService) {
        this.friendService = friendService;
        this.userService = userService;
    }

    @PostMapping("/api/v1/seed-friends/{id}")
    public ResponseEntity<String> seedFriends(@RequestParam(required = false) Long userId) {
        friendService.seedFriend(userId);

        return ResponseEntity.ok("Friend seeding completed successfully.");
    }

    @GetMapping("api/v1/friends/")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<FriendDetails>> getFriendsSortedByLocation(@RequestBody FriendSearchDto friendSearchDto) {
        User user = userService.getLoggedUser();

        List<FriendDetails> sortedFriends = userService.getUserFriendsSortedByLocation(user.getId(), friendSearchDto);
        return ResponseEntity.ok(sortedFriends);
    }
}
