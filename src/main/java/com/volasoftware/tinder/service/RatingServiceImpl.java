package com.volasoftware.tinder.service;

import com.volasoftware.tinder.dto.RatingDto;
import com.volasoftware.tinder.exception.NoSuchFriendForUserException;
import com.volasoftware.tinder.exception.UserDoesNotExistException;
import com.volasoftware.tinder.model.Rating;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final UserService userService;
    private final RatingRepository ratingRepository;

    public void rateFriend(RatingDto ratingDto) {
        User user = userService.getLoggedUser();

        User friend = userService.getById(ratingDto.getFriendId()).orElseThrow(
                () -> new UserDoesNotExistException("User with this id does not exist"));

        if (!userService.areFriends(user, friend)) {
            throw new NoSuchFriendForUserException("You are not friend with this user");
        }

        Rating existingRating = ratingRepository.findByUserAndFriend(user, friend);

        if (existingRating == null) {
            Rating newRating = new Rating();
            newRating.setUser(user);
            newRating.setFriend(friend);
            newRating.setRating(ratingDto.getRating());
            user.getRatings().add(newRating);
            ratingRepository.save(newRating);
        } else {
            existingRating.setRating(ratingDto.getRating());
            ratingRepository.save(existingRating);
        }

        userService.save(user);
    }
}
