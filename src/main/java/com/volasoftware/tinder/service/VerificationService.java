package com.volasoftware.tinder.service;

import com.volasoftware.tinder.exception.InvalidVerificationToken;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.Verification;
import com.volasoftware.tinder.repository.UserRepository;
import com.volasoftware.tinder.repository.VerificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerificationService{

    private final VerificationRepository verificationRepository;
    private final UserRepository userRepository;

    public void saveVerificationToken(Verification token){

        verificationRepository.save(token);

    }

    public VerificationService(VerificationRepository verificationRepository,
                               UserRepository userRepository) {
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
    }
    public boolean verifyUser(String token){

        Verification tokenEntity = verificationRepository.findByToken(token)
                .orElseThrow(() -> new InvalidVerificationToken("Invalid token"));

        if(tokenEntity.getExpirationDate().isAfter(LocalDateTime.now())){
            User userToVerify = tokenEntity.getUserId();

            userToVerify.setVerified(true);

            userRepository.save(userToVerify);

            return true;
        }

        return false;
    }
}
