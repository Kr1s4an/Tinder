package com.volasoftware.tinder.utility;

import com.volasoftware.tinder.model.Gender;
import com.volasoftware.tinder.model.Role;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.UserType;
import com.volasoftware.tinder.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

public class BotGenerator {
    public static void generate(int numberOfBots, UserRepository userRepository) {
        Set<User> botUsers = new HashSet<>();

        for (int i = 0; i <= numberOfBots; i++) {
            User botUser = createBotUser();
            botUsers.add(botUser);
        }
        userRepository.saveAll(botUsers);
    }

    private static User createBotUser() {
        User botUser = new User();
        botUser.setFirstName("BotFirstName");
        botUser.setLastName("BotLastName");
        botUser.setEmail("bot" + NumberGenerator.generateNumber() + "@example.com");
        botUser.setPassword("botpassword");
        botUser.setGender(Gender.MALE);
        botUser.setVerified(true);
        botUser.setRole(Role.USER);
        botUser.setAge(25);
        botUser.setType(UserType.BOT);

        return botUser;
    }
}
