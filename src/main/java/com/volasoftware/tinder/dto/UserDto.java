package com.volasoftware.tinder.dto;

import com.volasoftware.tinder.annotation.Password;
import com.volasoftware.tinder.model.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class UserDto {
    @NotBlank(message = "Name is mandatory")
    @Pattern(regexp = "^[A-Za-z]*$", message = "Must contain only letters")
    @Size(min = 2, max = 50, message = "The size must be between 2 and 50 letters")
    private String firstName;
    @NotBlank(message = "Name is mandatory")
    @Pattern(regexp = "^[A-Za-z]*$", message = "Must contain only letters")
    @Size(min = 2, max = 50, message = "The size must be between 2 and 50 letters")
    private String lastName;
    @NotBlank(message = "Password is mandatory")
    @Password
    private String password;
    @NotBlank(message = "Email is mandatory")
    @Column(unique = true)
    @Email
    private String email;
    private Gender gender;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

}
