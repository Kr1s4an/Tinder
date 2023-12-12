package com.volasoftware.tinder.service;

import com.volasoftware.tinder.exception.InvalidVerificationToken;
import com.volasoftware.tinder.exception.UserAlreadyVerifiedException;
import com.volasoftware.tinder.exception.UserDoesNotExistException;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.Verification;
import com.volasoftware.tinder.repository.UserRepository;
import com.volasoftware.tinder.repository.VerificationRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationService {
    private final VerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final EmailSenderService emailSender;
    private final EmailContentService emailContent;

    @Value("${localhost_verify}")
    private String localHostVerify;

    public VerificationService(VerificationRepository verificationRepository,
                               UserRepository userRepository,
                               EmailSenderService emailSender,
                               EmailContentService emailContent) {
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
        this.emailContent = emailContent;
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

        String content =  emailContent.createContent(localHostVerify + newToken.getToken(), "classpath:email/registrationEmail.html");

        emailSender.sendEmail(user, "Verification", content);
    }
}
