package com.volasoftware.tinder.service;

import com.volasoftware.tinder.exception.InvalidVerificationToken;
import com.volasoftware.tinder.exception.UserAlreadyVerifiedException;
import com.volasoftware.tinder.exception.UserDoesNotExistException;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.Verification;
import com.volasoftware.tinder.repository.UserRepository;
import com.volasoftware.tinder.repository.VerificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationService {
    private final VerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final UserServiceImpl userService;

    public VerificationService(VerificationRepository verificationRepository,
                               UserRepository userRepository,
                               JavaMailSender mailSender,
                               UserServiceImpl userService) {
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.userService = userService;
    }

    public void saveVerificationToken(Verification token) {
        verificationRepository.save(token);
    }

    public boolean verifyUser(String token) {
        Verification tokenEntity = verificationRepository.findByToken(token)
                .orElseThrow(() -> new InvalidVerificationToken("Invalid token"));
        if (tokenEntity.getExpirationDate().isAfter(LocalDateTime.now())) {
            User userToVerify = tokenEntity.getUser();
            userToVerify.setVerified(true);
            userRepository.save(userToVerify);

            return true;
        }

        return false;
    }

    public void resendEmail(String email) throws MessagingException, IOException {
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
