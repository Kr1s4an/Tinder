package com.volasoftware.tinder.controller;


import com.volasoftware.tinder.model.UserDto;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;

public class RegistrationController {

    @GetMapping("api/v1/users/register")
    public String registrationForm(WebRequest request, Model model){
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }

}
