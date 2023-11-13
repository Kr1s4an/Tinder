package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.exception.UserAlreadyVerifiedException;
import com.volasoftware.tinder.exception.UserDoesNotExistException;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.Verification;
import com.volasoftware.tinder.repository.UserRepository;
import com.volasoftware.tinder.repository.VerificationRepository;
import com.volasoftware.tinder.service.UserService;
import com.volasoftware.tinder.service.VerificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
public class VerificationController {

    private final VerificationService verificationService;
    private final VerificationRepository verificationRepository;
    private final JavaMailSender mailSender;
    private final UserService userService;
    private final UserRepository userRepository;

    public VerificationController(VerificationService verificationService,
                                  VerificationRepository verificationRepository,
                                  JavaMailSender javaMailSender,
                                  UserService userService,
                                  UserRepository userRepository) {
        this.verificationService = verificationService;
        this.verificationRepository = verificationRepository;
        this.mailSender = javaMailSender;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verifyUser(@PathVariable String token) {

        return (verificationService.verifyUser(token)) ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("api/v1/users/resend-verification-email")
    public void resendVerificationToken(String email) throws MessagingException, IOException {
        User user = userRepository.findOneByEmail(email).orElseThrow(
                () -> new UserDoesNotExistException("User with this email does not exist")
        );
        if (user.isVerified()) {
            throw new UserAlreadyVerifiedException("User is already verified");
        }
        Optional<Verification> oldTokenEntity = verificationRepository.findTokenByUserId(user.getId());
        oldTokenEntity.ifPresent(verificationRepository::delete);

        Verification newToken = new Verification();
        newToken.setUser(user);
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setCreatedDate(LocalDateTime.now());
        newToken.setExpirationDate(LocalDateTime.now().plusDays(2));
        verificationRepository.saveAndFlush(newToken);

        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress("kristinmpetkov@gmail.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, user.getEmail());
        message.setSubject("Verification");
        message.setContent(userService.getEmailContent(newToken.getToken()), "text/html; charset=utf-8");
        mailSender.send(message);
    }
}
