package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.dto.FriendProfileDto;
import com.volasoftware.tinder.dto.FriendSearchDto;
import com.volasoftware.tinder.model.FriendDetails;
import com.volasoftware.tinder.service.FriendService;
import com.volasoftware.tinder.service.UserService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    public FriendController(FriendService friendService,
                            UserService userService) {
        this.friendService = friendService;
        this.userService = userService;
    }

    @PostMapping("/seed/{id}")
    public ResponseEntity<String> seedFriends(@RequestParam(required = false) Long userId) {
        friendService.seedFriend(userId);

        return ResponseEntity.ok("Friend seeding completed successfully.");
    }

    @GetMapping("/")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<FriendDetails>> getFriendsSortedByLocation(@RequestBody FriendSearchDto friendSearchDto) {
        List<FriendDetails> sortedFriends = userService.getUserFriendsSortedByLocation(friendSearchDto);

        return ResponseEntity.ok(sortedFriends);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<FriendProfileDto> getFriendsProfile(Long friendId) {
        FriendProfileDto friend = userService.findFriendById(friendId);
        return ResponseEntity.ok(friend);
    }
}
