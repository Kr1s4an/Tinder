package com.volasoftware.tinder.model;

import lombok.NoArgsConstructor;

public enum Gender {
    FEMALE("female","src/main/resources/female.png"),
    MALE("male","src/main/resources/male.png"),
    OTHER("other","src/main/resources/other.png");

    private final String gender;
    private final String imgPath;

    Gender(String gender, String imgPath) {
        this.gender = gender;
        this.imgPath = imgPath;
    }
}
