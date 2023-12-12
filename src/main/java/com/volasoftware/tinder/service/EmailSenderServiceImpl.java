package com.volasoftware.tinder.service;

import com.volasoftware.tinder.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    @Value("${email_from}")
    private String emailFrom;

    public EmailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(User user, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(emailFrom));
        message.setRecipients(MimeMessage.RecipientType.TO, user.getEmail());
        message.setSubject(subject);
        message.setContent(content, "text/html; charset=utf-8");
        mailSender.send(message);
    }
}
