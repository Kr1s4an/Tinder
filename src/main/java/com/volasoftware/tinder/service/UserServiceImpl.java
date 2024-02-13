package com.volasoftware.tinder.service;

import com.volasoftware.tinder.dto.*;
import com.volasoftware.tinder.exception.*;
import com.volasoftware.tinder.mapper.UserMapper;
import com.volasoftware.tinder.model.*;
import com.volasoftware.tinder.repository.UserRepository;
import com.volasoftware.tinder.repository.VerificationRepository;
import com.volasoftware.tinder.utility.BotGenerator;
import com.volasoftware.tinder.utility.FriendLinker;
import com.volasoftware.tinder.utility.PasswordEncoder;
import com.volasoftware.tinder.utility.PasswordGenerator;
import jakarta.mail.MessagingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final EmailSenderService emailSender;
    private final EmailContentService emailContent;
    private final UserMapper userMapper;

    @Value("${localhost_verify}")
    private String localHostVerify;

    public UserServiceImpl(
            UserRepository userRepository,
            VerificationRepository verificationRepository,
            EmailSenderService emailSender,
            EmailContentService emailContent, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.emailSender = emailSender;
        this.emailContent = emailContent;
        this.userMapper = userMapper;
    }

    public User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        return userRepository.findOneByEmail(currentUser).orElseThrow(() ->
                new NotLoggedInException("You are not logged in!"));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void saveAll(Collection<User> users) {
        userRepository.saveAll(users);
    }

    public List<FriendDetails> getUserFriendsSortedByLocation(FriendSearchDto friendSearchDto) {
        User user = getLoggedUser();

        if (friendSearchDto.getCurrentLongitude() == null || friendSearchDto.getCurrentLatitude() == null) {
            friendSearchDto.setCurrentLatitude(user.getLocation().getLatitude());
            friendSearchDto.setCurrentLongitude(user.getLocation().getLongitude());
        }

        return userRepository.findUserFriendsSortedByLocation(user.getId(),
                friendSearchDto.getCurrentLatitude(), friendSearchDto.getCurrentLongitude());
    }

    @Override
    public void registerUser(UserDto userDto) throws IOException, MessagingException {

        if (isEmailRegistered(userDto.getEmail())) {
            throw new EmailAlreadyRegisteredException("Email already exist!");
        }

        User user = userMapper.userDtoToUser(userDto);
        user.setPassword(PasswordEncoder.encodePassword(userDto.getPassword()));
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
    public Optional<User> findOneByEmail(String email) {
        return userRepository.findOneByEmail(email);
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

        return userMapper.userToUserProfileDto(user);
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

        return userMapper.userToUserProfileDto(user);
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

        if (!areFriends(user, friend)) {
            user.getFriends().add(friend);
            friend.getFriends().add(user);
            userRepository.save(user);
            userRepository.save(friend);
        }
    }

    public void removeFriend(Long friendId) {
        User user = getLoggedUser();
        User friend = userRepository.findById(friendId).orElseThrow(() -> new ResourceNotFoundException("User does not exist with the ID: " + friendId));

        if (areFriends(user, friend)) {
            user.getFriends().remove(friend);
            friend.getFriends().remove(user);
            userRepository.save(user);
            userRepository.save(friend);
        }
    }

    public List<User> getUsersByUserType(UserType userType) {
        return userRepository.findByType(userType);
    }

    public void linkRandomFriendsForNonBotUsers(List<User> nonBotUsers, List<User> botUsers) {
        FriendLinker.linkRandomFriendsForNonBotUsers(nonBotUsers, botUsers);

        userRepository.saveAll(nonBotUsers);
    }

    public FriendProfileDto findFriendById(Long friendId) {
        User loggedUser = getLoggedUser();

        User friend = userRepository.findById(friendId).orElseThrow(
                () -> new UserDoesNotExistException("Friend does not exist"));

        if (!areFriends(loggedUser, friend)) {
            throw new NoSuchFriendForUserException("You are not friends with this user");
        }

        return userMapper.userToFriendProfileDto(friend);
    }

    public boolean areFriends(User user1, User user2) {
        Set<User> friendsOfUser1 = user1.getFriends();
        Set<User> friendsOfUser2 = user2.getFriends();

        return friendsOfUser1.contains(user2) && friendsOfUser2.contains(user1);
    }

    public Page<FriendRatingDetails> findFriendsSortedByRating(int pageNumber, int pageSize) {
        User loggedUser = getLoggedUser();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("rating").descending());

        return userRepository.findFriendsSortedByRating(loggedUser.getId(), pageable);
    }

    @Async
    public void linkRandomFriendsAsync(User requestedUser) {
        int numberOfBots = 20;
        List<User> botUsers = userRepository.findByType(UserType.BOT);
        if (botUsers.isEmpty()) {
            botUsers = BotGenerator.generate(numberOfBots);
        }
        List<User> allUsers = new ArrayList<>(botUsers);
        allUsers.add(requestedUser);

        FriendLinker.linkRandomFriendsForRequestedUser(requestedUser, botUsers);
        userRepository.saveAll(allUsers);
    }
}
