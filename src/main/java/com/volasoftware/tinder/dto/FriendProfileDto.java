package com.volasoftware.tinder.dto;

import com.volasoftware.tinder.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendProfileDto {
    private String firstName;
    private String lastName;
    private Integer age;
    private Gender gender;
}