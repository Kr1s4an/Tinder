package com.volasoftware.tinder.service;

import com.volasoftware.tinder.dto.ChangePasswordDto;
import com.volasoftware.tinder.dto.LoginUserDto;
import com.volasoftware.tinder.dto.UserDto;
import com.volasoftware.tinder.dto.UserProfileDto;
import com.volasoftware.tinder.exception.*;
import com.volasoftware.tinder.model.Role;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.UserType;
import com.volasoftware.tinder.model.Verification;
import com.volasoftware.tinder.repository.UserRepository;
import com.volasoftware.tinder.repository.VerificationRepository;
import com.volasoftware.tinder.utility.FriendLinker;
import com.volasoftware.tinder.utility.PasswordEncoder;
import com.volasoftware.tinder.utility.PasswordGenerator;
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

    private User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        return userRepository.findOneByEmail(currentUser).orElseThrow(() ->
                new NotLoggedInException("You are not logged in!"));
    }

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
        User user = getLoggedUser();

        return new UserProfileDto(user.getFirstName(), user.getLastName(), user.getEmail(), user.getGender());
    }

    @Override
    public UserProfileDto updateUserProfile(UserProfileDto userProfileDto) {
        User user = getLoggedUser();

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

    public void updateUserPassword(ChangePasswordDto changePasswordDto) {
        User user = getLoggedUser();

        if (changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
            throw new PasswordDoesNotMatchException("Password does not match!");
        }

        user.setPassword(PasswordEncoder.encodePassword(changePasswordDto.getNewPassword()));
        userRepository.save(user);
    }

    public void addFriend(Long friendId) {
        User user = getLoggedUser();
        User friend = userRepository.findById(friendId).orElseThrow(() -> new ResourceNotFoundException("User does not exist with the ID: " + friendId));
        user.getFriends().add(friend);
        userRepository.save(user);
    }

    public void removeFriend(Long friendId) {
        User user = getLoggedUser();
        User friend = userRepository.findById(friendId).orElseThrow(() -> new ResourceNotFoundException("User does not exist with the ID: " + friendId));
        user.getFriends().remove(friend);
        userRepository.save(user);
    }

    public List<User> getUsersByUserType(UserType userType) {
        return userRepository.findByType(userType);
    }

    public List<User> linkRandomFriendsForNonBotUsers() {
        List<User> nonBotUsers = userRepository.findByType(UserType.REAL);
        List<User> botUsers = userRepository.findByType(UserType.BOT);

        FriendLinker.linkRandomFriendsForNonBotUsers(nonBotUsers, botUsers);

        return nonBotUsers;
    }

    public User linkRandomFriendsForRequestedUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User requestedUser = optionalUser.orElseThrow(() ->
                new UserDoesNotExistException("User with this id does not exist"));

        if (requestedUser != null) {
            List<User> botUsers = userRepository.findByType(UserType.BOT);
            FriendLinker.linkRandomFriendsForRequestedUser(requestedUser, botUsers);
        }

        return requestedUser;
    }
}
