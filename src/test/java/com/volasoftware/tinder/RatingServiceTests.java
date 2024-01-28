package com.volasoftware.tinder;

import com.volasoftware.tinder.dto.RatingDto;
import com.volasoftware.tinder.exception.NoSuchFriendForUserException;
import com.volasoftware.tinder.exception.UserDoesNotExistException;
import com.volasoftware.tinder.model.Rating;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.repository.RatingRepository;
import com.volasoftware.tinder.service.RatingServiceImpl;
import com.volasoftware.tinder.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RatingServiceTests {

    @Mock
    private UserService userService;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void rateFriend_WithNonExistingUser_ShouldThrowUserDoesNotExistException() {
        RatingDto ratingDto = new RatingDto();
        ratingDto.setFriendId(1L);
        ratingDto.setRating(5);

        when(userService.getLoggedUser()).thenReturn(new User());
        when(userService.getById(ratingDto.getFriendId())).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () -> ratingService.rateFriend(ratingDto));

        verify(userService, times(1)).getLoggedUser();
        verify(userService, times(1)).getById(ratingDto.getFriendId());
        verifyNoInteractions(ratingRepository);
    }

    @Test
    void rateFriend_WithNonFriendUser_ShouldThrowNoSuchFriendForUserException() {
        RatingDto ratingDto = new RatingDto();
        ratingDto.setFriendId(1L);
        ratingDto.setRating(5);

        when(userService.getLoggedUser()).thenReturn(new User());
        when(userService.getById(ratingDto.getFriendId())).thenReturn(Optional.of(new User()));

        assertThrows(NoSuchFriendForUserException.class, () -> ratingService.rateFriend(ratingDto));

        verify(userService, times(1)).getLoggedUser();
        verify(userService, times(1)).getById(ratingDto.getFriendId());
        verifyNoInteractions(ratingRepository);
    }

    @Test
    void rateFriend_WithExistingRating_ShouldUpdateRating() {
        RatingDto ratingDto = new RatingDto();
        ratingDto.setFriendId(1L);
        ratingDto.setRating(5);

        User loggedUser = new User();
        User friend = new User();
        Rating existingRating = new Rating();

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(userService.getById(ratingDto.getFriendId())).thenReturn(Optional.of(friend));
        when(userService.areFriends(loggedUser, friend)).thenReturn(true);
        when(ratingRepository.findByUserAndFriend(loggedUser, friend)).thenReturn(existingRating);

        ratingService.rateFriend(ratingDto);

        verify(userService, times(1)).getLoggedUser();
        verify(userService, times(1)).getById(ratingDto.getFriendId());
        verify(userService, times(1)).areFriends(loggedUser, friend);
        verify(userService, times(1)).save(loggedUser);
        verify(ratingRepository, times(1)).findByUserAndFriend(loggedUser, friend);
        verify(ratingRepository, times(1)).save(existingRating);
        verifyNoMoreInteractions(userService, ratingRepository);
    }

    @Test
    void rateFriend_WithNonExistingRating_ShouldCreateNewRating() {
        RatingDto ratingDto = new RatingDto();
        ratingDto.setFriendId(1L);
        ratingDto.setRating(5);

        User loggedUser = new User();
        User friend = new User();

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(userService.getById(ratingDto.getFriendId())).thenReturn(Optional.of(friend));
        when(userService.areFriends(loggedUser, friend)).thenReturn(true);
        when(ratingRepository.findByUserAndFriend(loggedUser, friend)).thenReturn(null);

        ratingService.rateFriend(ratingDto);

        verify(userService, times(1)).getLoggedUser();
        verify(userService, times(1)).getById(ratingDto.getFriendId());
        verify(userService, times(1)).areFriends(loggedUser, friend);
        verify(userService, times(1)).save(loggedUser);
        verify(ratingRepository, times(1)).findByUserAndFriend(loggedUser, friend);
        verify(ratingRepository, times(1)).save(any(Rating.class));
        verifyNoMoreInteractions(userService, ratingRepository);
    }
}
