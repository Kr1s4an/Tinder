package com.volasoftware.tinder;

import com.volasoftware.tinder.model.Gender;
import com.volasoftware.tinder.model.Role;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserRepositorySaveUserAndReturnSavedUser() {

        //Arranges
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("testtest");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setGender(Gender.MALE);
        user.setVerified(true);
        user.setRole(Role.USER);

        //Act
        User savedUser = userRepository.save(user);

        User existUser = entityManager.find(User.class, savedUser.getId());

        //Assert
        assertThat(user.getEmail()).isEqualTo(existUser.getEmail());
        assertThat(existUser).isNotNull();
    }

    @Test
    public void testUserRepositoryGetAllUsersAndReturnMoreThanOneUser() {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("testtest");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setGender(Gender.MALE);
        user.setVerified(true);
        user.setRole(Role.USER);

        User user2 = new User();
        user2.setFirstName("Test");
        user2.setLastName("Test");
        user2.setEmail("test2@gmail.com");
        user2.setPassword("testtest");
        user2.setGender(Gender.MALE);
        user2.setVerified(true);
        user2.setRole(Role.USER);

        userRepository.save(user);
        userRepository.save(user2);

        List<User> userList = userRepository.findAll();

        assertThat(userList).isNotNull();
        assertThat(userList.size()).isEqualTo(2);
    }

    @Test
    public void testUserRepositoryGetUserByEmailAndReturnTheUser() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("testtest");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setGender(Gender.MALE);
        user.setVerified(true);
        user.setRole(Role.USER);

        userRepository.save(user);

        User userList = userRepository.findOneByEmail(user.getEmail()).get();

        assertThat(userList).isNotNull();
    }
}
