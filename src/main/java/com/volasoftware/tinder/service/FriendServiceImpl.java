package com.volasoftware.tinder.service;

import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.UserType;
import com.volasoftware.tinder.repository.UserRepository;
import com.volasoftware.tinder.utility.BotGenerator;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        int numberOfBots = 20;

        List<User> allUsers = new ArrayList<>();

        if (botUsers.isEmpty()) {
            Set<User> generatedBots = BotGenerator.generate(numberOfBots);
            userRepository.saveAll(generatedBots);
        }

        if (userId == null) {
            List<User> nonBotUsers = userService.linkRandomFriendsForNonBotUsers();
            allUsers.addAll(nonBotUsers);
        } else {
            User requestedUser = userService.linkRandomFriendsForRequestedUser(userId);
            if (requestedUser != null) {
                allUsers.add(requestedUser);
            }
            userRepository.saveAll(allUsers);
        }
    }
}
