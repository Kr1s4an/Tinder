package com.volasoftware.tinder.service;

import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.Verification;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailSenderService {
    public String getVerificationEmailContent(String token) throws IOException;

    public void sendVerificationEmail(Verification token, User user) throws MessagingException, IOException;
}
