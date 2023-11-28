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
    public void testUserRepositorySaveUserReturnSavedUser(){

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

    }

}
