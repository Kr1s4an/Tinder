package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.dto.FriendProfileDto;
import com.volasoftware.tinder.dto.FriendSearchDto;
import com.volasoftware.tinder.dto.RatingDto;
import com.volasoftware.tinder.model.FriendDetails;
import com.volasoftware.tinder.model.FriendRatingDetails;
import com.volasoftware.tinder.service.FriendService;
import com.volasoftware.tinder.service.RatingService;
import com.volasoftware.tinder.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;
    private final RatingService ratingService;

    public FriendController(FriendService friendService,
                            UserService userService,
                            RatingService ratingService) {
        this.friendService = friendService;
        this.userService = userService;
        this.ratingService = ratingService;
    }

    @PostMapping("/seed/{id}")
    public ResponseEntity<String> seedFriends(@RequestParam(required = false) Long userId) {
        friendService.seedFriend(userId);

        return ResponseEntity.ok("Friend seeding completed successfully.");
    }

    @GetMapping("/")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<FriendDetails>> getFriendsSortedByLocation(FriendSearchDto friendSearchDto) {
        List<FriendDetails> sortedFriends = userService.getUserFriendsSortedByLocation(friendSearchDto);

        return ResponseEntity.ok(sortedFriends);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<FriendProfileDto> getFriendsProfile(Long friendId) {
        FriendProfileDto friend = userService.findFriendById(friendId);
        return ResponseEntity.ok(friend);
    }

    @PostMapping("/rate")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity rateFriends(@RequestBody RatingDto ratingDto) {
        ratingService.rateFriend(ratingDto);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/rating")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Page<FriendRatingDetails>> getFriendsSortedByRating(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "5", required = false) int size) {

        Page<FriendRatingDetails> sortedFriends = userService.findFriendsSortedByRating(page, size);

        return ResponseEntity.ok(sortedFriends);
    }
}
