package com.volasoftware.tinder.service;

import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.Verification;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailSenderService {
    String getVerificationEmailContent(String token) throws IOException;

    void sendVerificationEmail(Verification token, User user) throws MessagingException, IOException;

    String getForgotPasswordEmailContent(String password) throws IOException;

    void sendForgotPasswordEmail(User user) throws MessagingException, IOException;
}
