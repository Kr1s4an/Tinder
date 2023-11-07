package com.volasoftware.tinder.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.TemporalType.TIMESTAMP;

@Entity
@Table(name = "verification_tokens")
public class Verification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "TOKEN")
    private String token;
    @Temporal(TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
    @Temporal(TIMESTAMP)
    @Column(name = "EXPIRATION_DATE")
    private LocalDateTime expirationDate;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID")
    private User userId;

    public User getId() {
        return userId;
    }

    public void setId(User userId) {
        this.userId = userId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }
}
