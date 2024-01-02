package com.volasoftware.tinder.service;

import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.UserType;
import com.volasoftware.tinder.repository.UserRepository;
import com.volasoftware.tinder.utility.BotGenerator;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {

    private final UserService userService;
    private final UserRepository userRepository;

    public FriendServiceImpl(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public void seedFriend(@RequestParam(required = false) Long user_id) {
        List<User> botUsers = userService.getUsersByUserType(UserType.BOT);

        if (botUsers.isEmpty()) {
            BotGenerator.generateBotUsers(20, userRepository);
        }

        if (user_id == null) {
            userService.linkRandomFriendsForNonBotUsers();
        } else {
            userService.linkRandomFriendsForRequestedUser(user_id);
        }
    }
}
