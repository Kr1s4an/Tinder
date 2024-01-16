package com.volasoftware.tinder.utility;

import com.volasoftware.tinder.model.*;

import java.util.ArrayList;
import java.util.List;

public class BotGenerator {
    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;

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

    private static Location createLocationForUser(User user) {
        Location location = new Location();

        location.setUser(user);
        location.setLatitude(RandomCoordinatesGenerator.generate(MIN_LATITUDE, MAX_LATITUDE));
        location.setLongitude(RandomCoordinatesGenerator.generate(MIN_LONGITUDE, MAX_LONGITUDE));

        return location;
    }
}
