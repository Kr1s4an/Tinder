package com.volasoftware.tinder.service;

import com.volasoftware.tinder.exception.UserDoesNotExistException;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.UserType;
import com.volasoftware.tinder.utility.BotGenerator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {

    private final UserService userService;

    public FriendServiceImpl(UserService userService) {
        this.userService = userService;
    }

    public void seedFriend(@RequestParam(required = false) Long userId) {
        List<User> users = new ArrayList<>();
        if (userId != null) {
            users.add(userService.getById(userId).orElseThrow(() -> new UserDoesNotExistException("User with this id does not exist")));
        } else {
            users.addAll(userService.getUsersByUserType(UserType.REAL));
        }

        if (!CollectionUtils.isEmpty(users)) {
            List<User> botUsers = userService.getUsersByUserType(UserType.BOT);
            int numberOfBots = 20;

            if (botUsers.isEmpty()) {
                botUsers = BotGenerator.generate(numberOfBots);
                userService.saveAll(botUsers);
            }

            userService.linkRandomFriendsForNonBotUsers(users, botUsers);
        }
    }
}
