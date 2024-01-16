package com.volasoftware.tinder.model;

public class FriendDetailsImpl implements FriendDetails {
    private final String firstName;
    private final String lastName;
    private final Integer age;
    private final Double distanceInKm;

    public FriendDetailsImpl(String firstName, String lastName, Integer age, Double distanceInKm) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.distanceInKm = distanceInKm;
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
    public Double getDistanceInKm() {
        return distanceInKm;
    }
}
