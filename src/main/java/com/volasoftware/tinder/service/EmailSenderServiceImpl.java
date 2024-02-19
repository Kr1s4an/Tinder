package com.volasoftware.tinder.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    @Value("${email_from}")
    private String emailFrom;

    public void sendEmail(String email, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(emailFrom));
        message.setRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject(subject);
        message.setContent(content, "text/html; charset=utf-8");
        mailSender.send(message);
    }
}
