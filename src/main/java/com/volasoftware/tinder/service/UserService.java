package com.volasoftware.tinder.service;

import com.volasoftware.tinder.dto.ChangePasswordDto;
import com.volasoftware.tinder.dto.LoginUserDto;
import com.volasoftware.tinder.dto.UserDto;
import com.volasoftware.tinder.dto.UserProfileDto;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.UserType;
import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAll();

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

    List<User> linkRandomFriendsForNonBotUsers();

    User linkRandomFriendsForRequestedUser(Long userId);
}
