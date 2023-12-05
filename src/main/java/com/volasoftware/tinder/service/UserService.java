package com.volasoftware.tinder.service;

import com.volasoftware.tinder.dto.LoginUserDto;
import com.volasoftware.tinder.dto.UserDto;
import com.volasoftware.tinder.dto.UserProfileDto;
import com.volasoftware.tinder.model.User;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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

    UserProfileDto editUserProfile(@RequestBody UserProfileDto userProfileDto);

    void getNewGeneratedPassword(String email) throws MessagingException, IOException;
}
