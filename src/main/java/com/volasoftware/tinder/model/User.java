package com.volasoftware.tinder.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "USER")
public class User extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "FIRST_NAME", nullable = false, length = 50)
    private String firstName;
    @Column(name = "LAST_NAME", nullable = false, length = 50)
    private String lastName;
    @Column(name = "EMAIL", nullable = false, unique = true, length = 45)
    private String email;
    @Column(name = "PASSWORD", nullable = false, length = 64)
    private String password;
    @Column(name = "GENDER")
    @Enumerated(value = EnumType.STRING)
    Gender gender;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
