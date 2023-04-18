package com.volasoftware.tinder.controller;


import com.volasoftware.tinder.dto.UserDto;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/v1/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getById(id).get();
    }

    @PostMapping("/api/v1/users/register")
    public ResponseEntity registerNewUser(@Valid @RequestBody UserDto userDto) {
        userService.registerUser(userDto);

        return ResponseEntity.ok(HttpStatus.OK);
    }

}
