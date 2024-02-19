package com.volasoftware.tinder.dto;

import com.volasoftware.tinder.model.Gender;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
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
public class UserProfileDto {
    @Pattern(regexp = "^[A-Za-z]*$", message = "Must contain only letters")
    @Size(min = 2, max = 50, message = "The size must be between 2 and 50 letters")
    private String firstName;
    @Pattern(regexp = "^[A-Za-z]*$", message = "Must contain only letters")
    @Size(min = 2, max = 50, message = "The size must be between 2 and 50 letters")
    private String lastName;
    @Column(unique = true)
    @Email
    private String email;
    private Gender gender;
}
