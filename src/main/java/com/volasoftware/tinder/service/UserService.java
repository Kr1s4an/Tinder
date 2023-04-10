package com.volasoftware.tinder.service;

import com.volasoftware.tinder.dto.UserDto;
import com.volasoftware.tinder.model.Gender;
import com.volasoftware.tinder.model.User;
import com.volasoftware.tinder.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public void registerUser(UserDto userDto){
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(userDto.getPassword());
        user.setGender(Gender.valueOf(userDto.getGender()));
        userRepository.save(user);
    }

    public Optional<User> getById(Long id){
        return userRepository.findById(id);
    }

}
