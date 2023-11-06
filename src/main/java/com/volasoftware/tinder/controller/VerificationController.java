package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.service.VerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verifyUser(@PathVariable String token){
        ResponseEntity<?> testToken = verificationService.verifyUser(token) ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        System.out.println(token);

        return testToken;
    }

}
