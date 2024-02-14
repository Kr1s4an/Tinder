package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.service.VerificationService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verifyUser(@PathVariable String token) {

        return (verificationService.verifyUser(token)) ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("api/v1/users/resend-verification-email")
    public ResponseEntity resendVerificationToken(String email) throws MessagingException, IOException {
        verificationService.resendEmail(email);

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
