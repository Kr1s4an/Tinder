package com.volasoftware.tinder.utility;

import com.volasoftware.tinder.model.Gender;
import com.volasoftware.tinder.model.Role;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.UserType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BotGenerator {
    public static Set<User> generateBotUsers(int numberOfBots) {
        Set<User> botUsers = new HashSet<>();

        for (int i = 0; i <= numberOfBots; i++) {
            User botUser = createBotUser();
            botUsers.add(botUser);
        }

        return botUsers;
    }

    private static User createBotUser() {
        User botUser = new User();
        botUser.setFirstName("BotFirstName");
        botUser.setLastName("BotLastName");
        botUser.setEmail("bot" + UUID.randomUUID() + "@example.com");
        botUser.setPassword("botpassword");
        botUser.setGender(Gender.MALE);
        botUser.setVerified(true);
        botUser.setRole(Role.USER);
        botUser.setAge(25);
        botUser.setType(UserType.BOT);

        return botUser;
    }
}
