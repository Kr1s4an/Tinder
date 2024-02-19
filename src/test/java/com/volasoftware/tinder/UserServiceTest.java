package com.volasoftware.tinder;

import com.volasoftware.tinder.dto.FriendProfileDto;
import com.volasoftware.tinder.dto.FriendSearchDto;
import com.volasoftware.tinder.dto.UserDto;
import com.volasoftware.tinder.exception.EmailAlreadyRegisteredException;
import com.volasoftware.tinder.exception.NoSuchFriendForUserException;
import com.volasoftware.tinder.exception.UserDoesNotExistException;
import com.volasoftware.tinder.mapper.UserMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Mock
    UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    public void testUserServiceRegisterNewUserAndReturnTheUser() throws MessagingException, IOException {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setPassword("password");
        userDto.setGender(Gender.MALE);

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(userDto.getPassword());
        user.setGender(userDto.getGender());
        user.setRole(Role.USER);

        Verification verification = new Verification();
        verification.setUser(user);
        verification.setToken("token");
        verification.setCreatedDate(LocalDateTime.now());
        verification.setExpirationDate(LocalDateTime.now().plusDays(2));

        emailSender.sendEmail(user.getEmail(), "Subject", "content");

        when(userRepository.findOneByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.userDtoToUser(userDto)).thenReturn(user);
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

        loggedUser.getFriends().add(friendToRemove);

        friendToRemove.getFriends().add(loggedUser);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.of(loggedUser));
        when(userRepository.findById(friendToRemove.getId())).thenReturn(Optional.of(friendToRemove));

        // Act
        userServiceImpl.removeFriend(friendToRemove.getId());

        // Assert
        verify(userRepository, times(1)).save(eq(loggedUser));
        verify(userRepository, times(1)).save(eq(friendToRemove));
        assertFalse(loggedUser.getFriends().contains(friendToRemove));
        assertFalse(friendToRemove.getFriends().contains(loggedUser));
    }

    @Test
    public void testGetUserFriendsSortedByLocation() {
        User loggedUser = new User();
        loggedUser.setId(1L);

        FriendSearchDto friendSearchDto = new FriendSearchDto();
        friendSearchDto.setCurrentLatitude(20.0);
        friendSearchDto.setCurrentLongitude(15.0);

        FriendDetailsImpl mockFriend = new FriendDetailsImpl("John", "Doe", 25, 10.5);


        List<FriendDetails> mockFriends = List.of(mockFriend);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("someUsername");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.of(loggedUser));
        when(userRepository.findUserFriendsSortedByLocation(
                eq(loggedUser.getId()), eq(friendSearchDto.getCurrentLatitude()), eq(friendSearchDto.getCurrentLongitude())))
                .thenReturn(mockFriends);

        List<FriendDetails> result = userServiceImpl.getUserFriendsSortedByLocation(friendSearchDto);

        assertEquals(1, result.size());
        assertEquals(mockFriend.getFirstName(), result.get(0).getFirstName());
        assertEquals(mockFriend.getLastName(), result.get(0).getLastName());
        verify(userRepository, times(1)).findUserFriendsSortedByLocation(
                eq(loggedUser.getId()), eq(friendSearchDto.getCurrentLatitude()), eq(friendSearchDto.getCurrentLongitude()));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testGetUserFriendsSortedByLocationWithNoFriends() {
        User loggedUser = new User();
        loggedUser.setId(1L);

        FriendSearchDto friendSearchDto = new FriendSearchDto();
        friendSearchDto.setCurrentLatitude(20.0);
        friendSearchDto.setCurrentLongitude(15.0);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("someUsername");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.of(loggedUser));
        when(userRepository.findUserFriendsSortedByLocation(
                loggedUser.getId(), friendSearchDto.getCurrentLatitude(), friendSearchDto.getCurrentLongitude()))
                .thenReturn(Collections.emptyList());

        List<FriendDetails> result = userServiceImpl.getUserFriendsSortedByLocation(friendSearchDto);

        assertEquals(0, result.size());
        verify(userRepository, times(1)).findUserFriendsSortedByLocation(
                eq(loggedUser.getId()), eq(friendSearchDto.getCurrentLatitude()), eq(friendSearchDto.getCurrentLongitude()));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testGetUserFriendsSortedByLocationWithMultipleFriends() {
        User loggedUser = new User();
        loggedUser.setId(1L);

        FriendSearchDto friendSearchDto = new FriendSearchDto();
        friendSearchDto.setCurrentLatitude(20.0);
        friendSearchDto.setCurrentLongitude(15.0);

        FriendDetailsImpl mockFriend1 = new FriendDetailsImpl("John", "Doe", 25, 10.5);
        FriendDetailsImpl mockFriend2 = new FriendDetailsImpl("Mikael", "Jackson", 13, 13.5);
        FriendDetailsImpl mockFriend3 = new FriendDetailsImpl("Pesho", "Ivanov", 56, 16.8);

        List<FriendDetails> mockFriends = Arrays.asList(mockFriend1, mockFriend2, mockFriend3);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("someUsername");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.of(loggedUser));
        when(userRepository.findUserFriendsSortedByLocation(
                eq(loggedUser.getId()), eq(friendSearchDto.getCurrentLatitude()), eq(friendSearchDto.getCurrentLongitude())))
                .thenReturn(mockFriends);

        List<FriendDetails> result = userServiceImpl.getUserFriendsSortedByLocation(friendSearchDto);

        assertEquals(3, result.size());
        assertEquals(mockFriend1.getFirstName(), result.get(0).getFirstName());
        assertEquals(mockFriend1.getLastName(), result.get(0).getLastName());
        assertEquals(mockFriend2.getFirstName(), result.get(1).getFirstName());
        assertEquals(mockFriend2.getLastName(), result.get(1).getLastName());
        assertEquals(mockFriend3.getFirstName(), result.get(2).getFirstName());
        assertEquals(mockFriend3.getLastName(), result.get(2).getLastName());
        verify(userRepository, times(1)).findUserFriendsSortedByLocation(
                eq(loggedUser.getId()), eq(friendSearchDto.getCurrentLatitude()), eq(friendSearchDto.getCurrentLongitude()));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testFindFriendById() {
        User loggedUser = new User();
        loggedUser.setId(2L);

        User friend = new User();
        friend.setId(1L);
        friend.setFirstName("John");
        friend.setLastName("Doe");
        friend.setAge(25);
        friend.setGender(Gender.MALE);

        FriendProfileDto friendProfileDto = new FriendProfileDto();
        friendProfileDto.setFirstName(friend.getFirstName());
        friendProfileDto.setLastName(friend.getLastName());
        friendProfileDto.setAge(friend.getAge());
        friendProfileDto.setGender(friend.getGender());

        Set<User> friendsOfLoggedUser = new HashSet<>();
        Set<User> friendsOfFriend = new HashSet<>();

        friendsOfLoggedUser.add(friend);
        friendsOfFriend.add(loggedUser);

        loggedUser.setFriends(friendsOfLoggedUser);
        friend.setFriends(friendsOfFriend);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("someUsername");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.of(loggedUser));
        when(userRepository.findById(friend.getId())).thenReturn(Optional.of(friend));
        when(userMapper.userToFriendProfileDto(friend)).thenReturn(friendProfileDto);

        userServiceImpl.findFriendById(friend.getId());

        assertNotNull(friendProfileDto);
        assertEquals(friend.getFirstName(), friendProfileDto.getFirstName());
        assertEquals(friend.getLastName(), friendProfileDto.getLastName());
        assertEquals(friend.getAge(), friendProfileDto.getAge());
        assertEquals(friend.getGender(), friendProfileDto.getGender());
        verify(userRepository, times(1)).findById(friend.getId());
        verify(userMapper, times(1)).userToFriendProfileDto(friend);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testFindFriendByIdWithNonExistingFriend() {
        Long nonExistingFriendId = 999L;

        User loggedUser = new User();
        loggedUser.setId(2L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("someUsername");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.of(loggedUser));
        when(userRepository.findById(nonExistingFriendId)).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () -> userServiceImpl.findFriendById(nonExistingFriendId));
    }

    @Test
    public void testFindFriendByIdWithNotFriends() {
        Long friendId = 1L;

        User loggedUser = new User();
        loggedUser.setId(2L);

        User friend = new User();
        friend.setId(friendId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("someUsername");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.of(loggedUser));
        when(userRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        NoSuchFriendForUserException exception = assertThrows(NoSuchFriendForUserException.class,
                () -> userServiceImpl.findFriendById(friendId));

        assertEquals("You are not friends with this user", exception.getMessage());
    }

    @Test
    public void testFindFriendsSortedByRating() {
        User loggedUser = new User();
        loggedUser.setId(1L);

        FriendRatingDetailsImpl mockFriend = new FriendRatingDetailsImpl("John", "Doe", 25, 10);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("rating")));

        List<FriendRatingDetails> mockFriends = List.of(mockFriend);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("someUsername");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.of(loggedUser));
        when(userRepository.findFriendsSortedByRating(eq(loggedUser.getId()), eq(pageRequest)))
                .thenReturn(new PageImpl<>(mockFriends, pageRequest, mockFriends.size()));

        Page<FriendRatingDetails> result = userServiceImpl.findFriendsSortedByRating(
                pageRequest.getPageNumber(), pageRequest.getPageSize());

        assertEquals(1, result.getTotalElements());
        assertEquals(mockFriend.getFirstName(), result.getContent().get(0).getFirstName());
        assertEquals(mockFriend.getLastName(), result.getContent().get(0).getLastName());
        verify(userRepository, times(1)).findFriendsSortedByRating(loggedUser.getId(), pageRequest);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testFindFriendsSortedByRatingWihNoFriends() {
        User loggedUser = new User();
        loggedUser.setId(1L);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("rating").descending());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("someUsername");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.of(loggedUser));

        when(userRepository.findFriendsSortedByRating(eq(loggedUser.getId()), eq(pageRequest)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<FriendRatingDetails> result = userServiceImpl.findFriendsSortedByRating(
                pageRequest.getPageNumber(), pageRequest.getPageSize());

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(userRepository, times(1)).findFriendsSortedByRating(loggedUser.getId(), pageRequest);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testFindFriendsSortedByRatingWithMultipleFriends() {
        User loggedUser = new User();
        loggedUser.setId(1L);

        FriendRatingDetailsImpl mockFriend1 = new FriendRatingDetailsImpl("John", "Doe", 25, 10);
        FriendRatingDetailsImpl mockFriend2 = new FriendRatingDetailsImpl("Vin", "Diesel", 43, 8);
        FriendRatingDetailsImpl mockFriend3 = new FriendRatingDetailsImpl("Paul", "Walker", 43, 7);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("rating")));

        List<FriendRatingDetails> mockFriends = Arrays.asList(mockFriend1, mockFriend2, mockFriend3);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("someUsername");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.of(loggedUser));
        when(userRepository.findFriendsSortedByRating(eq(loggedUser.getId()), eq(pageRequest)))
                .thenReturn(new PageImpl<>(mockFriends, pageRequest, mockFriends.size()));

        Page<FriendRatingDetails> result = userServiceImpl.findFriendsSortedByRating(
                pageRequest.getPageNumber(), pageRequest.getPageSize());

        assertEquals(3, result.getTotalElements());
        assertEquals(mockFriend1.getFirstName(), result.getContent().get(0).getFirstName());
        assertEquals(mockFriend1.getLastName(), result.getContent().get(0).getLastName());
        assertEquals(mockFriend2.getFirstName(), result.getContent().get(1).getFirstName());
        assertEquals(mockFriend2.getLastName(), result.getContent().get(1).getLastName());
        assertEquals(mockFriend3.getFirstName(), result.getContent().get(2).getFirstName());
        assertEquals(mockFriend3.getLastName(), result.getContent().get(2).getLastName());
        verify(userRepository, times(1)).findFriendsSortedByRating(loggedUser.getId(), pageRequest);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testLinkRandomFriendsAsync() {
        User user = new User();
        user.setId(1L);
        user.setType(UserType.REAL);

        User botUser1 = new User();
        botUser1.setId(2L);
        botUser1.setType(UserType.BOT);

        User botUser2 = new User();
        botUser2.setId(3L);
        botUser2.setType(UserType.BOT);

        List<User> botUsers = Arrays.asList(botUser1, botUser2);

        List<User> allUsers = new ArrayList<>(botUsers);
        allUsers.add(user);

        when(userRepository.findByType(UserType.BOT)).thenReturn(botUsers);

        userServiceImpl.linkRandomFriendsAsync(user);

        verify(userRepository, times(1)).findByType(UserType.BOT);
        verify(userRepository, times(1)).saveAll(allUsers);
        verifyNoMoreInteractions(userRepository);
    }
}
