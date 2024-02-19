package com.volasoftware.tinder.dto;

import com.volasoftware.tinder.annotation.Password;
import com.volasoftware.tinder.model.Gender;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
