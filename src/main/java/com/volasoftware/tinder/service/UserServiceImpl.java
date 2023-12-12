package com.volasoftware.tinder.service;

import com.volasoftware.tinder.dto.LoginUserDto;
import com.volasoftware.tinder.dto.UserDto;
import com.volasoftware.tinder.dto.UserProfileDto;
import com.volasoftware.tinder.exception.*;
import com.volasoftware.tinder.model.Role;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.Verification;
import com.volasoftware.tinder.repository.UserRepository;
import com.volasoftware.tinder.repository.VerificationRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(userDto.getPassword()));
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

        emailSender.sendEmail(user, "Verification", content);
    }

    @Override
    public User loginUser(LoginUserDto input) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = userRepository.findOneByEmail(input.getEmail()).orElseThrow(
                () -> new UserDoesNotExistException("User with this email does not exist"));
        if (!passwordEncoder.matches(input.getPassword(),
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        User user = userRepository.findOneByEmail(currentUser).orElseThrow(() ->
                new NotLoggedInException("You are not logged in!"));

        return new UserProfileDto(user.getFirstName(), user.getLastName(), user.getEmail(), user.getGender());
    }

    @Override
    public UserProfileDto editUserProfile(@RequestBody UserProfileDto userProfileDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        User user = userRepository.findOneByEmail(currentUser).orElseThrow(() ->
                new NotLoggedInException("You are not logged in!"));

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

        user.setPassword(PasswordGenerator.generatePassword());
        String content = emailContent.createContent(user.getPassword(), "classpath:email/forgotPasswordEmail.html");
        emailSender.sendEmail(user, "Forgot Password", content);

        userRepository.save(user);
    }
}
