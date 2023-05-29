package com.volasoftware.tinder.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationController {

    @GetMapping("/verify/{token}")
    public ResponseEntity<String> verifyUser(@PathVariable String token){

        return ResponseEntity.ok("user verified: " + token);

    }

}
