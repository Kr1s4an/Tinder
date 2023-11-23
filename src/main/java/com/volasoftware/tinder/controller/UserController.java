package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.dto.UserProfileDto;
import com.volasoftware.tinder.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/v1/users/show-profile")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseBody
    public UserProfileDto getUserProfile() {

        return userService.getCurrentUserProfile();
    }

    @PostMapping("/api/v1/users/edit-profile")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity editUser(UserProfileDto userProfileDto){
        userService.editUserProfile(userProfileDto);

        return ResponseEntity.ok(HttpStatus.OK );
    }
}
