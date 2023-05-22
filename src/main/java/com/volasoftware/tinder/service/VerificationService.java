package com.volasoftware.tinder.service;

import com.volasoftware.tinder.model.Verification;
import com.volasoftware.tinder.repository.VerificationRepository;
import org.springframework.stereotype.Service;

@Service
public class VerificationService{

    private final VerificationRepository verificationRepository;

    public void saveVerificationToken(Verification token){

        verificationRepository.save(token);

    }

    public VerificationService(VerificationRepository verificationRepository) {
        this.verificationRepository = verificationRepository;
    }
}
