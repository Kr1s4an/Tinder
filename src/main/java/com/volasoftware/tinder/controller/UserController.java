package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.dto.ChangePasswordDto;
import com.volasoftware.tinder.dto.UserProfileDto;
import com.volasoftware.tinder.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseBody
    public UserProfileDto getUserProfile() {

        return userService.getCurrentUserProfile();
    }

    @PutMapping("/profile")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserProfileDto> editUser(@Valid @RequestBody UserProfileDto userProfileDto) {
        return new ResponseEntity<>(userService.updateUserProfile(userProfileDto), HttpStatus.OK);
    }

    @PostMapping("/password-recovery")
    public ResponseEntity forgotPassword(String email) throws MessagingException, IOException {
        userService.generateNewPasswordForUser(email);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/password")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity editPassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        userService.updateUserPassword(changePasswordDto);

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
