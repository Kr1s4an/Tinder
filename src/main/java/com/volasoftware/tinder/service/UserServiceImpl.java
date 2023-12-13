package com.volasoftware.tinder.service;

import com.volasoftware.tinder.dto.ChangePasswordDto;
import com.volasoftware.tinder.dto.LoginUserDto;
import com.volasoftware.tinder.dto.UserDto;
import com.volasoftware.tinder.dto.UserProfileDto;
import com.volasoftware.tinder.exception.*;
import com.volasoftware.tinder.model.Role;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.Verification;
import com.volasoftware.tinder.repository.UserRepository;
import com.volasoftware.tinder.repository.VerificationRepository;
import com.volasoftware.tinder.utility.PasswordEncoder;
import com.volasoftware.tinder.utility.PasswordGenerator;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.mail.MessagingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final EmailSenderService emailSender;
    private final EmailContentService emailContent;

    @Value("${localhost_verify}")
    private String localHostVerify;

    public UserServiceImpl(
            UserRepository userRepository,
            VerificationRepository verificationRepository,
            EmailSenderService emailSender,
            EmailContentService emailContent) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.emailSender = emailSender;
        this.emailContent = emailContent;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void registerUser(UserDto userDto) throws IOException, MessagingException {

        if (isEmailRegistered(userDto.getEmail())) {
            throw new EmailAlreadyRegisteredException("Email already exist!");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(PasswordEncoder.encodePassword(userDto.getPassword()));
        user.setGender(userDto.getGender());
        user.setRole(Role.USER);
        userRepository.save(user);

        Verification token = new Verification();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setCreatedDate(LocalDateTime.now());
        token.setExpirationDate(LocalDateTime.now().plusDays(2));
        verificationRepository.saveAndFlush(token);

        String content = emailContent.createContent(localHostVerify + token.getToken(), "classpath:email/registrationEmail.html");

        emailSender.sendEmail(user.getEmail(), "Verification", content);
    }

    @Override
    public User loginUser(LoginUserDto input) {
        User user = userRepository.findOneByEmail(input.getEmail()).orElseThrow(
                () -> new UserDoesNotExistException("User with this email does not exist"));
        if (!PasswordEncoder.equals(input.getPassword(),
                user.getPassword())) {
            throw new PasswordDoesNotMatchException("Password does not match");
        }
        if (!user.isVerified()) {
            throw new UserIsNotVerifiedException("The email is not verified");
        }
        return user;
    }

    @Override
    public Optional<User> getById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean isEmailRegistered(String email) {
        return userRepository.findOneByEmail(email).isPresent();
    }

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findOneByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserProfileDto getCurrentUserProfile() {
       User user = loggedUser();

        return new UserProfileDto(user.getFirstName(), user.getLastName(), user.getEmail(), user.getGender());
    }

    @Override
    public UserProfileDto editUserProfile(UserProfileDto userProfileDto) {
        User user = loggedUser();

        if (StringUtils.isNotEmpty(userProfileDto.getFirstName())) {
            user.setFirstName(userProfileDto.getFirstName());
        }
        if (StringUtils.isNotEmpty(userProfileDto.getLastName())) {
            user.setLastName(userProfileDto.getLastName());
        }
        if (StringUtils.isNotEmpty(userProfileDto.getEmail())) {
            user.setEmail(userProfileDto.getEmail());
        }
        if (userProfileDto.getGender() != null) {
            user.setGender(userProfileDto.getGender());
        }

        user = userRepository.save(user);

        return new UserProfileDto(user.getFirstName(), user.getLastName(), user.getEmail(), user.getGender());
    }

    public void generateNewPasswordForUser(String email) throws MessagingException, IOException {
        User user = userRepository.findOneByEmail(email).orElseThrow(
                () -> new UserDoesNotExistException("User with this email does not exist"));

        String generatedPassword = PasswordGenerator.generatePassword();
        user.setPassword(PasswordEncoder.encodePassword(generatedPassword));
        userRepository.save(user);

        String content = emailContent.createContent(generatedPassword, "classpath:email/forgotPasswordEmail.html");
        emailSender.sendEmail(user.getEmail(), "Forgot Password", content);
    }

    public void editUserPassword(ChangePasswordDto changePasswordDto) {
        User user = loggedUser();

        if (changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
            throw new PasswordDoesNotMatchException("Password does not match!");
        }

        user.setPassword(PasswordEncoder.encodePassword(changePasswordDto.getNewPassword()));
        userRepository.save(user);
    }
    private User loggedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        return userRepository.findOneByEmail(currentUser).orElseThrow(() ->
                new NotLoggedInException("You are not logged in!"));
    }
}
