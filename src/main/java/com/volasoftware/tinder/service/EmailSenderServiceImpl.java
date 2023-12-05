package com.volasoftware.tinder.service;

import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.Verification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final ResourceLoader resourceLoader;
    private final JavaMailSender mailSender;

    public EmailSenderServiceImpl(ResourceLoader resourceLoader,
                                  JavaMailSender mailSender) {
        this.resourceLoader = resourceLoader;
        this.mailSender = mailSender;
    }

    public String getVerificationEmailContent(String token) throws IOException {
        Resource emailResource = resourceLoader.getResource("classpath:email/registrationEmail.html");
        File emailFile = emailResource.getFile();
        Path path = Path.of(emailFile.getPath());
        String emailContent = Files.readString(path);

        return emailContent.replace("{{token}}", "http://localhost:8080/verify/" + token);
    }

    public void sendVerificationEmail(Verification token, User user) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress("kristinmpetkov@gmail.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, user.getEmail());
        message.setSubject("Verification");
        message.setContent(getVerificationEmailContent(token.getToken()), "text/html; charset=utf-8");
        mailSender.send(message);
    }

    public String getForgotPasswordEmailContent(String password) throws IOException {
        Resource emailResource = resourceLoader.getResource("classpath:email/forgotPasswordEmail.html");
        File emailFile = emailResource.getFile();
        Path path = Path.of(emailFile.getPath());
        String emailContent = Files.readString(path);

        return emailContent.replace("{{password}}", password);
    }

    public void sendForgotPasswordEmail(User user) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress("kristinmpetkov@gmail.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, user.getEmail());
        message.setSubject("Forgot Password");
        message.setContent(getForgotPasswordEmailContent(user.getPassword()), "text/html; charset=utf-8");
        mailSender.send(message);
    }
}