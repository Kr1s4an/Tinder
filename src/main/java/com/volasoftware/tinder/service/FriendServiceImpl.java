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

    public void seedFriend(@RequestParam(required = false) Long userId) {
        List<User> botUsers = userService.getUsersByUserType(UserType.BOT);

        if (botUsers.isEmpty()) {
            BotGenerator.generate(20, userRepository);
        }

        if (userId == null) {
            userService.linkRandomFriendsForNonBotUsers();
        } else {
            userService.linkRandomFriendsForRequestedUser(userId);
        }
    }
}
