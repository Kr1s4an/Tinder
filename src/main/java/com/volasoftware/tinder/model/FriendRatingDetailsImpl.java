package com.volasoftware.tinder.model;

public class FriendRatingDetailsImpl implements FriendRatingDetails {
    private final String firstName;
    private final String lastName;
    private final Integer age;
    private final Integer rating;

    public FriendRatingDetailsImpl(String firstName, String lastName, Integer age, Integer rating) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.rating = rating;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public Integer getAge() {
        return age;
    }

    @Override
    public Integer getRating() {
        return rating;
    }
}
