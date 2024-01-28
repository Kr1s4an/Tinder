package com.volasoftware.tinder.service;

import com.volasoftware.tinder.dto.*;
import com.volasoftware.tinder.exception.NoSuchFriendForUserException;
import com.volasoftware.tinder.model.FriendDetails;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.UserType;
import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User getLoggedUser();

    List<User> getAll();

    void save(User user);

    void saveAll(Collection<User> users);

    List<FriendDetails> getUserFriendsSortedByLocation(FriendSearchDto friendSearchDto);

    void registerUser(UserDto userDto) throws IOException, MessagingException;

    User loginUser(LoginUserDto input);

    Optional<User> getById(long id);

    boolean isEmailRegistered(String email);

    UserDetailsService userDetailsService();

    UserProfileDto getCurrentUserProfile();

    UserProfileDto updateUserProfile(UserProfileDto userProfileDto);

    void generateNewPasswordForUser(String email) throws MessagingException, IOException;

    void updateUserPassword(ChangePasswordDto changePasswordDto);

    void addFriend(Long friendId);

    void removeFriend(Long friendId);

    List<User> getUsersByUserType(UserType userType);

    void linkRandomFriendsForNonBotUsers(List<User> nonBotUsers, List<User> botUsers);

    FriendProfileDto findFriendById(Long friendId) throws NoSuchFriendForUserException;

    boolean areFriends(User user1, User user2);
}
