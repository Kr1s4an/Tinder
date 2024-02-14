package com.volasoftware.tinder.controller;


import com.volasoftware.tinder.dto.UserDto;
import com.volasoftware.tinder.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class RegistrationController {
    private final UserService userService;

    @PostMapping("/api/v1/users/register")
    public ResponseEntity registerNewUser(@Valid @RequestBody UserDto userDto) throws IOException, MessagingException {
        userService.registerUser(userDto);

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
