package com.volasoftware.tinder.utility;

import com.volasoftware.tinder.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BotGenerator {
    public static List<User> generate(int numberOfBots) {
        List<User> botUsers = new ArrayList<>();

        for (int i = 0; i < numberOfBots; i++) {
            User botUser = createBot();
            botUser.setLocation(createLocationForUser(botUser));
            botUsers.add(botUser);
        }

        return botUsers;
    }

    private static User createBot() {
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

    private static Location createLocationForUser(User user){
        Random random = new Random();
        Location location = new Location();

        location.setUser(user);
        location.setLatitude(random.nextDouble());
        location.setLongitude(random.nextDouble());

        return location;
    }
}
