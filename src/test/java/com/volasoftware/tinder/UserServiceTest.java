package com.volasoftware.tinder;

import com.volasoftware.tinder.dto.FriendSearchDto;
import com.volasoftware.tinder.dto.UserDto;
import com.volasoftware.tinder.exception.EmailAlreadyRegisteredException;
import com.volasoftware.tinder.exception.UserDoesNotExistException;
import com.volasoftware.tinder.model.*;
import com.volasoftware.tinder.repository.UserRepository;
import com.volasoftware.tinder.repository.VerificationRepository;
import com.volasoftware.tinder.service.EmailContentService;
import com.volasoftware.tinder.service.EmailSenderService;
import com.volasoftware.tinder.service.UserServiceImpl;
import com.volasoftware.tinder.utility.PasswordGenerator;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = UserServiceImpl.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    VerificationRepository verificationRepository;

    @Mock
    EmailSenderService emailSender;

    @Mock
    EmailContentService emailContent;

    @Mock
    Authentication authentication;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private static class FriendDetailsImpl implements FriendDetails {
        private final String firstName;
        private final String lastName;
        private final Integer age;
        private final Double distanceInKm;

        public FriendDetailsImpl(String firstName, String lastName, Integer age, Double distanceInKm) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.distanceInKm = distanceInKm;
        }

        @Override
        public String getFirstName() {
            return firstName;
        }

        @Override
        public String getLastName() {
            return lastName;
        }

        @Override
        public Integer getAge() {
            return age;
        }

        @Override
        public Double getDistanceInKm() {
            return distanceInKm;
        }
    }

    @Test
    public void testUserServiceRegisterNewUserAndReturnTheUser() throws MessagingException, IOException {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setPassword("password");
        userDto.setGender(Gender.MALE);

        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("hashed_password");
        user.setGender(Gender.MALE);
        user.setRole(Role.USER);

        Verification verification = new Verification();
        verification.setUser(user);
        verification.setToken("token");
        verification.setCreatedDate(LocalDateTime.now());
        verification.setExpirationDate(LocalDateTime.now().plusDays(2));

        emailSender.sendEmail(user.getEmail(), "Subject", "content");

        when(userRepository.findOneByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(verificationRepository.saveAndFlush(any(Verification.class))).thenReturn(new Verification());

        userServiceImpl.registerUser(userDto);

        verify(userRepository, times(1)).save(any(User.class));
        verify(verificationRepository, times(1)).saveAndFlush(any(Verification.class));
        verify(emailSender, times(1)).sendEmail(user.getEmail(), "Subject", "content");
    }

    @Test
    public void testRegisterUser_emailAlreadyRegistered() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setPassword("password");
        userDto.setGender(Gender.MALE);

        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("hashed_password");
        user.setGender(Gender.MALE);
        user.setRole(Role.USER);

        when(userRepository.findOneByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> userServiceImpl.registerUser(userDto)
        );

        assertEquals("Email already exist!", exception.getMessage());
    }

    @Test
    public void testNewPasswordForUser() throws MessagingException, IOException {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword(PasswordGenerator.generatePassword());
        String content = "content";
        when(userRepository.findOneByEmail(email)).thenReturn(Optional.of(user));
        when(emailContent.createContent(anyString(), anyString())).thenReturn(content);
        doNothing().when(emailSender).sendEmail(user.getEmail(), "Forgot Password", content);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        userServiceImpl.generateNewPasswordForUser(email);

        // Assert
        verify(userRepository, times(1)).findOneByEmail(email);
        verify(userRepository, times(1)).save(user);
        verify(emailSender, times(1)).sendEmail(user.getEmail(), "Forgot Password", "content");
    }

    @Test
    public void testNewPasswordForUser_userDoesNotExist() throws MessagingException {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findOneByEmail(email)).thenReturn(Optional.empty());

        // Act
        assertThrows(UserDoesNotExistException.class, () -> {
            userServiceImpl.generateNewPasswordForUser(email);
        });

        // Assert
        verify(userRepository, times(1)).findOneByEmail(email);
        verify(userRepository, never()).save(any(User.class));
        verify(emailSender, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testAddFriend() {
        //Arrange
        User loggedUser = new User();
        loggedUser.setId(1L);
        loggedUser.setFriends(new HashSet<>());

        Long friendId = 2L;
        User friend = new User();
        friend.setId(friendId);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.of(loggedUser));
        when(userRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        // Act
        userServiceImpl.addFriend(friendId);

        // Assert
        verify(userRepository, times(1)).findById(friendId);
        verify(userRepository, times(1)).save(loggedUser);
        assert loggedUser.getFriends().contains(friend);
    }

    @Test
    public void testRemoveFriend() {
        //Arrange
        User loggedUser = new User();
        loggedUser.setId(1L);

        User friendToRemove = new User();
        friendToRemove.setId(2L);
        Set<User> friends = new HashSet<>();
        friends.add(friendToRemove);
        loggedUser.setFriends(friends);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.of(loggedUser));
        when(userRepository.findById(friendToRemove.getId())).thenReturn(Optional.of(friendToRemove));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        //Act
        userServiceImpl.removeFriend(friendToRemove.getId());

        //Assert
        verify(userRepository, times(1)).save(loggedUser);
        assertFalse(loggedUser.getFriends().contains(friendToRemove));
    }

    @Test
    public void testGetUserFriendsSortedByLocation() {
        Long userId = 1L;
        FriendSearchDto friendSearchDto = new FriendSearchDto();

        when(userRepository.findUserFriendsSortedByLocation(eq(userId), eq(null), eq(null)))
                .thenReturn(Collections.singletonList(new FriendDetailsImpl("John", "Doe", 25, 10.5)));

        List<FriendDetails> result = userServiceImpl.getUserFriendsSortedByLocation(userId, friendSearchDto);

        assertEquals(1, result.size());
    }
}
