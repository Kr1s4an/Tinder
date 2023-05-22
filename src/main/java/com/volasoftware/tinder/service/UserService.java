package com.volasoftware.tinder.service;

import com.volasoftware.tinder.dto.UserDto;
import com.volasoftware.tinder.exception.EmailAlreadyRegisteredException;
import com.volasoftware.tinder.model.Gender;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.model.Verification;
import com.volasoftware.tinder.repository.UserRepository;
import com.volasoftware.tinder.repository.VerificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;

    public UserService(UserRepository userRepository, VerificationRepository verificationRepository) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public void registerUser(UserDto userDto){

        if(isEmailRegistered(userDto.getEmail())){
            throw new EmailAlreadyRegisteredException("Email already exist!");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(userDto.getPassword());
        user.setGender(Gender.valueOf(userDto.getGender()));
        userRepository.save(user);

        Verification token = new Verification();
        token.setUserId(user);
        token.setToken(UUID.randomUUID().toString());
        token.setCreatedDate(LocalDateTime.now());
        token.setExpirationDate(LocalDateTime.now().plusDays(2));
        verificationRepository.save(token);
    }

    public Optional<User> getById(Long id){
        return userRepository.findById(id);
    }

    public boolean isEmailRegistered(String email){
        return userRepository.findOneByEmail(email).isPresent();
    }

}
