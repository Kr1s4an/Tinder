package com.volasoftware.tinder.utility;

import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.UserType;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class FriendLinker {

    public static void linkRandomFriendsForNonBotUsers(List<User> nonBotUsers, List<User> botUsers) {
        for (User user : nonBotUsers) {
            linkRandomFriendsForRequestedUser(user, botUsers);
        }
    }

    public static void linkRandomFriendsForRequestedUser(User requestedUser, List<User> botUsers) {
        if (requestedUser != null && requestedUser.getType() != UserType.BOT && !botUsers.isEmpty()) {
            Random random = new Random();
            int randomNumberOfFriends = random.nextInt(botUsers.size());

            randomNumberOfFriends = Math.min(randomNumberOfFriends, botUsers.size());

            Set<User> friendsToAdd = generateUniqueRandomFriends(requestedUser, botUsers, randomNumberOfFriends);
            requestedUser.getFriends().addAll(friendsToAdd);

            for (User friendToAdd : friendsToAdd) {
                friendToAdd.getFriends().add(requestedUser);
            }
        }
    }

    private static Set<User> generateUniqueRandomFriends(User user, List<User> botUsers, int numberOfFriends) {
        Random random = new Random();
        Set<User> friendsToAdd = user.getFriends();

        while (friendsToAdd.size() < numberOfFriends) {
            int randomBotUserIndex = random.nextInt(botUsers.size());
            User randomBotUser = botUsers.get(randomBotUserIndex);

            if (!randomBotUser.equals(user)) {
                friendsToAdd.add(randomBotUser);
            }
        }

        return friendsToAdd;
    }
}
