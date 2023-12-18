package com.volasoftware.tinder.service;

import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.Verification;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailSenderService {

    void sendEmail(String email, String subject, String content) throws MessagingException;
}
