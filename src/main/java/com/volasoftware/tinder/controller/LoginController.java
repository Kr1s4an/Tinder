package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.jwt.JwtService;
import com.volasoftware.tinder.dto.LoginUserDto;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final UserServiceImpl userService;
    private final JwtService jwtService;

    public LoginController(UserServiceImpl userService,
                           JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/api/v1/users/login")
    public ResponseEntity loginUser(@Valid @RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = userService.loginUser(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        return ResponseEntity.ok(jwtToken);
    }
}
